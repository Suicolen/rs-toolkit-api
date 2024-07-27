package api.definition.skeletal.animation;

import api.definition.skeletal.math.FloatMaths;
import lombok.Data;

import java.util.Arrays;

import static api.definition.skeletal.math.FloatMaths.*;

@Data
public class AnimationCurve {

    private boolean isTimeInterpolate;

    private boolean isDisabled;

    private InfinityType preInfinityType;

    private InfinityType postInfinityType;

    private SkeletalKeyframe[] keyframes;

    private boolean isLinear;

    private float fX1;

    private float fX4;

    private final float[] fPolyX = new float[4];

    private final float[] fPolyY = new float[4];

    private boolean dirty = true;

    private int cachedKeyframeId = 0;

    public float[] values;

    private int startTime;

    private int endTime;

    private float minimumValue;

    private float maximumValue;

    // added (for encoding purposes)
    private CurveTransformType curveTransformType;

    public AnimationCurve() {
    }

    public void postDecode() {
        this.startTime = this.keyframes[0].getTime();
        this.endTime = this.keyframes[this.getKeyframeCount() - 1].getTime();
        this.values = new float[this.getDuration() + 1];
        for (int time = this.getStartTime(); time <= this.getEndTime(); time++) {
            this.values[time - this.getStartTime()] = evaluateCurve(this, (float) time);
        }
        //this.keyframes = null;
        this.minimumValue = evaluateCurve(this, ((float) (this.getStartTime() - 1)));
        this.maximumValue = evaluateCurve(this, ((float) (this.getEndTime() + 1)));
    }

    private static float evaluateCurve(AnimationCurve curve, float time) {
        if (curve != null && curve.getKeyframeCount() != 0) {
            if (time < ((float) (curve.keyframes[0].getTime()))) {
                return curve.preInfinityType == InfinityType.CONSTANT ? curve.keyframes[0].getValue() : evaluateInfinity(curve, time, true);
            } else if (time > ((float) (curve.keyframes[curve.getKeyframeCount() - 1].getTime()))) {
                return curve.postInfinityType == InfinityType.CONSTANT ? curve.keyframes[curve.getKeyframeCount() - 1].getValue() : evaluateInfinity(curve, time, false);
            } else if (curve.isDisabled) {
                return curve.keyframes[0].getValue();
            } else {
                SkeletalKeyframe keyframe = curve.getKeyframe(time);
                /*
                 * Determine whether the animCurve is static.
                 * The animCurve is considered to be static if it would return the same value regardless of the evaluation time.
                 * This basically means that the values of all the keys are the same and the y component of all the tangents is 0.
                 */
                boolean isStatic = false;
                boolean dontInterpolate = false;
                if (keyframe == null) {
                    return 0.0F;
                } else {
                    if ((double) keyframe.getTanOutX() == 0.0 && (double) keyframe.getTanOutY() == 0.0) {
                        isStatic = true;
                    } else if (keyframe.getTanOutX() == Float.MAX_VALUE && keyframe.getTanOutY() == Float.MAX_VALUE) {
                        dontInterpolate = true;
                    } else if (keyframe.getNext() != null) {
                        if (curve.dirty) {
                            float[] fXBuffer = new float[4];
                            float[] fYBuffer = new float[4];
                            fXBuffer[0] = ((float) (keyframe.getTime()));
                            fYBuffer[0] = keyframe.getValue();
                            fXBuffer[1] = fXBuffer[0] + keyframe.getTanOutX() * oneThird;
                            fYBuffer[1] = oneThird * keyframe.getTanOutY() + fYBuffer[0];
                            fXBuffer[3] = ((float) (keyframe.getNext().getTime()));
                            fYBuffer[3] = keyframe.getNext().getValue();
                            fXBuffer[2] = fXBuffer[3] - keyframe.getNext()
                                    .getTanInX() * oneThird;
                            fYBuffer[2] = fYBuffer[3] - oneThird * keyframe.getNext()
                                    .getTanInY();
                            if (curve.isTimeInterpolate) {
                                evaluate(curve, fXBuffer, fYBuffer);
                            } else {
                                curve.fX1 = fXBuffer[0];
                                // hermite interpolation
                                float dx = fXBuffer[3] - fXBuffer[0];
                                float dy = fYBuffer[3] - fYBuffer[0];
                                float tanX = fXBuffer[1] - fXBuffer[0];
                                float m1 = 0.0F;
                                float m2 = 0.0F;
                                if (((double) (tanX)) != 0.0) {
                                    m1 = (fYBuffer[1] - fYBuffer[0]) / tanX;
                                }
                                tanX = fXBuffer[3] - fXBuffer[2];
                                if (((double) (tanX)) != 0.0) {
                                    m2 = (fYBuffer[3] - fYBuffer[2]) / tanX;
                                }
                                float length = 1.0F / (dx * dx);
                                float double1 = m1 * dx;
                                float double2 = dx * m2;
                                curve.fPolyX[0] = length * (double1 + double2 - dy - dy) / dx;
                                curve.fPolyX[1] = (dy + dy + dy - double1 - double1 - double2) * length;
                                curve.fPolyX[2] = m1;
                                curve.fPolyX[3] = fYBuffer[0];
                            }
                            curve.dirty = false;
                        }
                    } else {
                        isStatic = true;
                    }
                    if (isStatic) {
                        return keyframe.getValue();
                    } else if (dontInterpolate) {
                        return ((float) (keyframe.getTime())) != time && keyframe.getNext() != null ? keyframe.getNext()
                                .getValue() : keyframe.getValue();
                    } else {
                        return curve.isTimeInterpolate ? evaluate(curve, time) : hermite(curve, time);
                    }
                }
            }
        } else {
            return 0.0F;
        }
    }

    // hermite
    private static float hermite(AnimationCurve curve, float time) {
        if (curve == null) {
            return 0.0F;
        } else {
            float t = time - curve.fX1;
            return t * (curve.fPolyX[2] + (t * curve.fPolyX[0] + curve.fPolyX[1]) * t) + curve.fPolyX[3];
        }
    }

    private static void evaluate(AnimationCurve curve, float[] xyValues1, float[] xyValues2) {
        if (curve != null) {
            float rangeX = xyValues1[3] - xyValues1[0];
            if (((double) (rangeX)) != 0.0) {
                float dx1 = xyValues1[1] - xyValues1[0];
                float dx2 = xyValues1[2] - xyValues1[0];
                // normalize X control values
                float[] nXValues = {dx1 / rangeX, dx2 / rangeX};
                // if all 4 CVs equally spaced, polynomial will be linear
                curve.isLinear = nXValues[0] == oneThird && nXValues[1] == twoThirds;
                // save the orig normalized control values
                float oldX1 = nXValues[0];
                float oldX2 = nXValues[1];
                // check the inside control values yield a monotonic function.
                // if they don't correct them with preference given to one of them.
                // Most of the time we are monotonic, so do some simple checks first
                if (((double) (nXValues[0])) < 0.0) {
                    nXValues[0] = 0.0F;
                }
                if (((double) (nXValues[1])) > 1.0) {
                    nXValues[1] = 1.0F;
                }
                if (((double) (nXValues[0])) > 1.0 || nXValues[1] < -1.0F) {
                    checkMonotonic(nXValues);
                }
                // compute the new control points
                if (nXValues[0] != oldX1) {
                    xyValues1[1] = xyValues1[0] + nXValues[0] * rangeX;
                    if (((double) (oldX1)) != 0.0) {
                        xyValues2[1] = xyValues2[0] + (xyValues2[1] - xyValues2[0]) * nXValues[0] / oldX1;
                    }
                }
                if (nXValues[1] != oldX2) {
                    xyValues1[2] = xyValues1[0] + nXValues[1] * rangeX;
                    if (1.0 != ((double) (oldX2))) {
                        xyValues2[2] = ((float) (((double) (xyValues2[3])) - ((double) (xyValues2[3] - xyValues2[2])) * (1.0 - ((double) (nXValues[1]))) / (1.0 - ((double) (oldX2)))));
                    }
                }
                curve.fX1 = xyValues1[0];
                curve.fX4 = xyValues1[3];
                float nx1 = nXValues[0];
                float nx2 = nXValues[1];
                // implements bezierToPower()
                // convert Tbezier basis to power basis
                float[] fPolyX = curve.fPolyX;
                float a = nx1 - 0.0F;
                float b = nx2 - nx1;
                float c = 1.0F - nx2;
                float d = b - a;
                fPolyX[3] = c - b - d; // a2
                fPolyX[2] = d + d + d; // b2
                fPolyX[1] = a + a + a; // c2
                fPolyX[0] = 0.0F; // d2
                a = xyValues2[0];
                b = xyValues2[1];
                c = xyValues2[2];
                d = xyValues2[3];
                float[] fPolyY = curve.fPolyY;
                float a2 = b - a;
                float b2 = c - b;
                float c2 = d - c;
                float d2 = b2 - a2;
                fPolyY[3] = c2 - b2 - d2;
                fPolyY[2] = d2 + d2 + d2;
                fPolyY[1] = a2 + a2 + a2;
                fPolyY[0] = a;
            }
        }
    }

    private static void checkMonotonic(float[] xValues) {
        // We want a control vector of [ 0 x1 (1-x2) 1 ] since this provides
        // more symmetry. (This yields better equations and constrains x1 and x2
        // to be positive.)
        xValues[1] = 1.0F - xValues[1];

        // x1 and x2 must always be positive
        if (xValues[0] < 0.0F) {
            xValues[0] = 0.0F;
        }

        if (xValues[1] < 0.0F) {
            xValues[1] = 0.0F;
        }

        // If x1 or x2 are greater than 1.0, then they must be inside the
        // ellipse (x1^2 + x2^2 - 2(x1 +x2) + x1*x2 + 1).
        // x1 and x2 are invalid if x1^2 + x2^2 - 2(x1 +x2) + x1*x2 + 1 > 0.0
        if (xValues[0] > 1.0F || xValues[1] > 1.0F) {
            float d = (float) (((double) xValues[1] - 2.0D) * (double) xValues[1] + (double) (xValues[0] * (xValues[0] - 2.0F + xValues[1])) + 1.0D);
            if (FloatMaths.epsilon + d > 0.0F) {
                constrainInsideBounds(xValues);
            }
        }

        xValues[1] = 1.0F - xValues[1];
    }

    private static void constrainInsideBounds(float[] xValues) {
        // (x1^2 + x2^2 - 2(x1 +x2) + x1*x2 + 1)
        //   = x2^2 + (x1 - 2)*x1 + (x1^2 - 2*x1 + 1)
        // Therefore, we solve for x2.
        if (xValues[0] + FloatMaths.epsilon < fourThirds) {
            float b = xValues[0] - 2.0F;
            float c = xValues[0] - 1.0F;
            float discr = (float) Math.sqrt((double) (b * b - c * 4.0F * c));
            float root = (-b + discr) * 0.5F;
            if (FloatMaths.epsilon + xValues[1] > root) {
                xValues[1] = root - FloatMaths.epsilon;
            } else {
                root = (-b - discr) * 0.5F;
                if (xValues[1] < FloatMaths.epsilon + root) {
                    xValues[1] = root + FloatMaths.epsilon;
                }
            }
        } else {
            xValues[0] = fourThirds - FloatMaths.epsilon;
            xValues[1] = oneThird - FloatMaths.epsilon;
        }
    }

    /**
     * Evaluate infinities of a single curve
     * @param curve    the animation curve
     * @param time     time to evaluate at
     * @param infinity either pre or post infinity
     * @return
     */
    private static float evaluateInfinity(AnimationCurve curve, float time, boolean infinity) {
        float value = 0.0F;
        if (curve != null && curve.getKeyframeCount() != 0) {
            float firstKeyframeTime = ((float) (curve.keyframes[0].getTime()));
            float lastKeyframeTime = ((float) (curve.keyframes[curve.getKeyframeCount() - 1].getTime()));
            float range = lastKeyframeTime - firstKeyframeTime;
            if (((double) (range)) == 0.0) {
                return curve.keyframes[0].getValue();
            } else {
                float ratio;
                if (time > lastKeyframeTime) {
                    ratio = (time - lastKeyframeTime) / range;
                } else {
                    ratio = (time - firstKeyframeTime) / range;
                }
                // modf implementation
                double ratioInt = (int) ratio;
                float remainder = Math.abs(((float) (((double) (ratio)) - ratioInt)));
                float factoredTime = remainder * range;
                ratioInt = Math.abs(ratioInt + 1.0);
                double ratioHalf = ratioInt / 2.0;
                double ratioHalfInt = (int) ratioHalf;
                remainder = ((float) (ratioHalf - ratioHalfInt));
                float tanTime;
                float tanValue;
                if (infinity) {
                    if (curve.preInfinityType == InfinityType.OSCILLATE) {
                        if (((double) (remainder)) != 0.0) {
                            factoredTime += firstKeyframeTime;
                        } else {
                            factoredTime = lastKeyframeTime - factoredTime;
                        }
                    } else if (curve.preInfinityType != InfinityType.CYCLE && curve.preInfinityType != InfinityType.CYCLE_RELATIVE) {
                        if (curve.preInfinityType == InfinityType.LINEAR) {
                            factoredTime = firstKeyframeTime - time;
                            tanTime = curve.keyframes[0].getTanInX();
                            tanValue = curve.keyframes[0].getTanInY();
                            value = curve.keyframes[0].getValue();
                            if (((double) (tanTime)) != 0.0) {
                                value -= tanValue * factoredTime / tanTime;
                            }
                            return value;
                        }
                    } else {
                        factoredTime = lastKeyframeTime - factoredTime;
                    }
                } else if (curve.postInfinityType == InfinityType.OSCILLATE) {
                    if (((double) (remainder)) != 0.0) {
                        factoredTime = lastKeyframeTime - factoredTime;
                    } else {
                        factoredTime += firstKeyframeTime;
                    }
                } else if (curve.postInfinityType != InfinityType.CYCLE && curve.postInfinityType != InfinityType.CYCLE_RELATIVE) {
                    if (curve.postInfinityType == InfinityType.LINEAR) {
                        factoredTime = time - lastKeyframeTime;
                        tanTime = curve.keyframes[curve.getKeyframeCount() - 1].getTanOutX();
                        tanValue = curve.keyframes[curve.getKeyframeCount() - 1].getTanOutY();
                        value = curve.keyframes[curve.getKeyframeCount() - 1].getValue();
                        if (((double) (tanTime)) != 0.0) {
                            value += factoredTime * tanValue / tanTime;
                        }
                        return value;
                    }
                } else {
                    factoredTime += firstKeyframeTime;
                }
                value = AnimationCurve.evaluateCurve(curve, factoredTime);

                float valueRange;
                if (infinity && curve.preInfinityType == InfinityType.CYCLE_RELATIVE) {
                    valueRange = curve.keyframes[curve.getKeyframeCount() - 1].getValue() - curve.keyframes[0].getValue();
                    value = ((float) (((double) (value)) - ((double) (valueRange)) * ratioInt));
                } else if (!infinity && curve.postInfinityType == InfinityType.CYCLE_RELATIVE) {
                    valueRange = curve.keyframes[curve.getKeyframeCount() - 1].getValue() - curve.keyframes[0].getValue();
                    value = ((float) (((double) (value)) + ((double) (valueRange)) * ratioInt));
                }
                return value;
            }
        } else {
            return value;
        }
    }

    /**
     * Evaluates the value of an animation curve at a given time
     * @param animationCurve the animation curve to evaluate
     * @param time the time at which to evaluate the animation curve
     * @return the value of the animation curve at the specified time
     */
    private static float evaluate(AnimationCurve animationCurve, float time) {
        System.out.println("-->");
        if (animationCurve == null) {
            return 0.0F;
        } else {
            float s;
            if (time == animationCurve.fX1) {
                s = 0.0F;
            } else if (animationCurve.fX4 == time) {
                s = 1.0F;
            } else {
                s = (time - animationCurve.fX1) / (animationCurve.fX4 - animationCurve.fX1);
            }
            float t;
            if (animationCurve.isLinear) {
                t = s;
            } else {
                // temporarily make X(t) = X(t) - s
                float[] poly = new float[]{animationCurve.fPolyX[0] - s, animationCurve.fPolyX[1], animationCurve.fPolyX[2], animationCurve.fPolyX[3]};
                // find the roots of the polynomial.  We are looking for only one
                // in the interval [0, 1]
                float[] roots = new float[5];
                int numRoots = polyZeroes(poly, 3, 0.0F, true, 1.0F, true, roots);
                if (numRoots == 1) {
                    t = roots[0];
                } else {
                    t = 0.0F;
                }
            }
            return t * (animationCurve.fPolyY[1] + t * (animationCurve.fPolyY[2] + animationCurve.fPolyY[3] * t)) + animationCurve.fPolyY[0];
        }
    }

    /**
     * Finds roots of a polynomial within a specified interval
     * @param poly the coefficients of the polynomial
     * @param deg the degree of the polynomial
     * @param a the lower bound of the interval
     * @param aClosed whether the lower bound is inclusive
     * @param b the upper bound of the interval
     * @param bClosed whether the upper bound is inclusive
     * @param roots roots found within the interval [a, b]
     * @return the number of roots found within the interval [a, b], -1 if none are found
     */
    private static int polyZeroes(float[] poly, int deg, float a, boolean aClosed, float b, boolean bClosed, float[] roots) {
        float f = 0.0F;
        for (int idx = 0; idx < deg + 1; ++idx) {
            f += Math.abs(poly[idx]);
        }
        float tolerance = (Math.abs(a) + Math.abs(b)) * ((float) (deg + 1)) * FloatMaths.epsilon;
        /* Zero polynomial to tolerance? */
        if (f <= tolerance) {
            return -1;
        } else {
            // normalize the polynomial ( by absolute values)
            // V_aA( 1.0/f, Poly, coefficients, deg+1 );
            float[] coefficients = new float[deg + 1];
            int nr;
            for (nr = 0; nr < deg + 1; ++nr) {
                coefficients[nr] = poly[nr] * (1.0F / f);
            }
            /* determine true degree */
            while (Math.abs(coefficients[deg]) < tolerance) {
                --deg;
            }
            /* Identically zero poly already caught so constant fn != 0 */
            nr = 0;
            if (deg == 0) {
                return nr;
                /* check for linear case */
            } else if (deg == 1) {
                roots[0] = -coefficients[0] / coefficients[1];
                boolean leftOk = (aClosed) ? a < tolerance + roots[0] : a < roots[0] - tolerance;
                boolean rightOk = (bClosed) ? b > roots[0] - tolerance : b > roots[0] + tolerance;
                nr = (leftOk && rightOk) ? 1 : 0;
                if (nr > 0) {
                    if (aClosed && roots[0] < a) {
                        roots[0] = a;
                    } else if (bClosed && roots[0] > b) {
                        roots[0] = b;
                    }
                }
                return nr;
            } else {
                /* handle non-linear case */
                Polynomial polynomial = new Polynomial(coefficients, deg);
                float[] d = new float[deg + 1];
                /* compute derivative */
                for (int index = 1; index <= deg; index++) {
                    d[index - 1] = coefficients[index] * ((float) (index));
                }
                float[] dr = new float[deg + 1];
                /* find roots of derivative */
                int ndr = polyZeroes(d, deg - 1, a, false, b, false, dr);
                if (ndr == -1) {
                    return 0;
                } else {
                    boolean skip = false;
                    float pe = 0.0F;
                    float ps = 0.0F;
                    float end = 0.0F;
                    /* find roots between roots of the derivative */
                    for (int index = 0; index <= ndr; index++) {
                        if (nr > deg) {
                            return nr;
                        }
                        float start;
                        if (index == 0) {
                            start = a;
                            ps = horner1(coefficients, deg, a);
                            if (Math.abs(ps) <= tolerance && aClosed) {
                                roots[nr++] = a;
                            }
                        } else {
                            start = end;
                            ps = pe;
                        }
                        if (index == ndr) {
                            end = b;
                            skip = false;
                        } else {
                            end = dr[index];
                        }
                        pe = horner1(coefficients, deg, end);
                        if (skip) {
                            skip = false;
                        } else if (Math.abs(pe) < tolerance) {
                            if (index != ndr || bClosed) {
                                roots[nr++] = end;
                                skip = true;
                            }
                        } else if (ps < 0.0F && pe > 0.0F || ps > 0.0F && pe < 0.0F) {
                            roots[nr++] = zeroin(polynomial, start, end, 0.0F);
                            if (nr > 1 && roots[nr - 2] >= roots[nr - 1] - tolerance) {
                                roots[nr - 2] = (roots[nr - 2] + roots[nr - 1]) * 0.5F;
                                --nr;
                            }
                        }
                    }
                    return nr;
                }
            }
        }
    }

    /**
     * @param polynomial the polynomial
     * @param a the lower bound of the interval
     * @param b the upper bound of the interval
     * @param tolerance the tolerance
     * @return
     */
    private static float zeroin(Polynomial polynomial, float a, float b, float tolerance) {
        float fa = horner1(polynomial.getCoefficients(), polynomial.getDeg(), a);
        if (Math.abs(fa) < FloatMaths.epsilon) {
            return a;
        } else {
            float fb = horner1(polynomial.getCoefficients(), polynomial.getDeg(), b);
            if (Math.abs(fb) < FloatMaths.epsilon) {
                return b;
            } else {
                /* horner2 impl */
                // brents method?
                float c = 0.0F;
                float d = 0.0F;
                float e = 0.0F;
                float fc = 0.0F;
                boolean resetValuesFlag = true;
                boolean continueIteration;
                do {
                    continueIteration = false;
                    if (resetValuesFlag) {
                        c = a;
                        fc = fa;
                        d = b - a;
                        e = d;
                        resetValuesFlag = false;
                    }
                    if (Math.abs(fc) < Math.abs(fb)) {
                        a = b;
                        b = c;
                        c = a;
                        fa = fb;
                        fb = fc;
                        fc = fa;
                    }
                    /* convergence test */
                    float del = FloatMaths.dblEpsilon * Math.abs(b) + tolerance * 0.5F;
                    float m = (c - b) * 0.5F;
                    boolean test = Math.abs(m) > del && 0.0F != fb;
                    if (test) {
                        if (!(Math.abs(e) < del) && !(Math.abs(fa) <= Math.abs(fb))) {
                            float s = fb / fa;
                            float p;
                            float q;
                            if (a == c) {
                                /* linear interpolation */
                                p = 2.0F * m * s;
                                q = 1.0F - s;
                            } else {
                                /* inverse quadratic interpolation */
                                q = fa / fc;
                                float r = fb / fc;
                                p = (2.0F * m * q * (q - r) - (r - 1.0F) * (b - a)) * s;
                                q = (q - 1.0F) * (r - 1.0F) * (s - 1.0F);
                            }
                            /* adjust the sign */
                            if (((double) (p)) > 0.0) {
                                q = -q;
                            } else {
                                p = -p;
                            }
                            /* check if interpolation is acceptable */
                            s = e;
                            e = d;
                            if (2.0F * p < m * 3.0F * q - Math.abs(q * del) && p < Math.abs(q * 0.5F * s)) {
                                d = p / q;
                            } else {
                                d = m;
                                e = m;
                            }
                        } else {
                            /* bisection */
                            d = m;
                            e = m;
                        }
                        /* complete step */
                        a = b;
                        fa = fb;
                        if (Math.abs(d) > del) {
                            b += d;
                        } else if (((double) (m)) > 0.0) {
                            b += del;
                        } else {
                            b -= del;
                        }
                        fb = horner1(polynomial.getCoefficients(), polynomial.getDeg(), b);
                        if (((double) (fb * (fc / Math.abs(fc)))) > 0.0) {
                            resetValuesFlag = true;
                            continueIteration = true;
                        } else {
                            continueIteration = true;
                        }
                    }
                } while (continueIteration);
                return b;
            }
        }
    }

    /**
     * Evaluates a polynomial using horner's method
     * @param coefficients the coefficients of the polynomial
     * @param deg the degree of the polynomial
     * @param s the value at which the polynomial is evaluated
     * @return the value of the polynomial at the given point 's'
     */
    private static float horner1(float[] coefficients, int deg, float s) {
        float accumulated = coefficients[deg];
        for (int index = deg - 1; index >= 0; --index) {
            accumulated = s * accumulated + coefficients[index];
        }
        return accumulated;
    }

    public float getValue(int time) {
        if (time < this.getStartTime()) {
            return this.minimumValue;
        } else {
            return time > this.getEndTime() ? this.maximumValue : this.values[time - this.getStartTime()];
        }
    }

    public int getDuration() {
        return this.getEndTime() - this.getStartTime();
    }

    private int getKeyframeByTime(float time) {
        if (this.cachedKeyframeId < 0 || !(((float) (this.keyframes[this.cachedKeyframeId].getTime())) <= time) || this.keyframes[this.cachedKeyframeId].getNext() != null && !(((float) (this.keyframes[this.cachedKeyframeId].getNext()
                .getTime())) > time)) {
            if (!(time < ((float) (this.getStartTime()))) && !(time > ((float) (this.getEndTime())))) {
                int count = this.getKeyframeCount();
                int selected = this.cachedKeyframeId;
                if (count > 0) {
                    int start = 0;
                    int end = count - 1;
                    // binary search
                    do {
                        int middle = start + end >> 1;
                        if (time < ((float) (this.keyframes[middle].getTime()))) {
                            if (time > ((float) (this.keyframes[middle - 1].getTime()))) {
                                selected = middle - 1;
                                break;
                            }
                            end = middle - 1;
                        } else {
                            if (!(time > ((float) (this.keyframes[middle].getTime())))) {
                                selected = middle;
                                break;
                            }
                            if (time < ((float) (this.keyframes[middle + 1].getTime()))) {
                                selected = middle;
                                break;
                            }
                            start = middle + 1;
                        }
                    } while (start <= end);
                }
                if (selected != this.cachedKeyframeId) {
                    this.cachedKeyframeId = selected;
                    this.dirty = true;
                }
                return this.cachedKeyframeId;
            } else {
                return -1;
            }
        } else {
            return this.cachedKeyframeId;
        }
    }

    private SkeletalKeyframe getKeyframe(float time) {
        int index = this.getKeyframeByTime(time);
        return index >= 0 && index < this.keyframes.length ? this.keyframes[index] : null;
    }

    public int getKeyframeCount() {
        return this.keyframes == null ? 0 : this.keyframes.length;
    }
}
