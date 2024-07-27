package api.definition.texture;

import api.definition.Definition;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public final class RSTexture implements Definition {

    private int innerWidth;
    private int innerHeight;
    private int xOffset;
    private int yOffset;
    private int width;
    private int height;
    private int[] pixels;
    private int flags;

    // OSRS
    private int id;
    private int frame;

    private int[] palette;

    private byte[] paletteIndices;
    private byte[] alphas;

    public static final int FLAG_VERTICAL = 0b01;
    public static final int FLAG_ALPHA = 0b10;

    private OSRSTextureDefinition osrsDefinition;

    public RSTexture() {

    }

    public int getId() {
        return osrsDefinition != null ? osrsDefinition.getId() : id;
    }

    public RSTexture copy() {
        RSTexture copy = new RSTexture();
        copy.innerWidth = innerWidth;
        copy.innerHeight = innerHeight;
        copy.xOffset = xOffset;
        copy.yOffset = yOffset;
        copy.width = width;
        copy.height = height;
        copy.pixels = pixels != null ? Arrays.copyOf(pixels, pixels.length) : null;
        copy.flags = flags;
        copy.id = id;
        copy.frame = frame;
        copy.palette = palette != null ? Arrays.copyOf(palette, palette.length) : null;
        copy.paletteIndices = paletteIndices != null ? Arrays.copyOf(paletteIndices, paletteIndices.length) : null;
        copy.alphas = alphas != null ? Arrays.copyOf(alphas, alphas.length) : null;
        copy.osrsDefinition = osrsDefinition != null ? osrsDefinition.copy() : null;
        return copy;
    }

    // think of a better name
    public void initOSRSTextureIfNeeded() {
        if (palette == null) {
            generatePaletteAndIndices();
        }

        if (osrsDefinition == null) {
            OSRSTextureDefinition osrsDef = new OSRSTextureDefinition();
            osrsDef.setField1786(new int[]{0});
            osrsDefinition = osrsDef;
            osrsDef.setId(id);
        }
    }

    public void generatePaletteAndIndices() {
        List<Integer> colors = new ArrayList<>();
        colors.add(0);
        for (int pixel : pixels) {
            if (!colors.contains(pixel)) {
                colors.add(pixel);
            }
        }

        if (colors.size() > 256) { // can be 256 instead of 255 cause the first one is used and we write paletteLength - 1
            System.out.println(STR."Too many colors: \{colors.size()}");
            return;
        }

        palette = new int[colors.size()];

        for (int i = 1; i < colors.size(); i++) {
            palette[i] = colors.get(i);
        }


        paletteIndices = new byte[innerWidth * innerHeight];
        for (int i = 0; i < pixels.length; i++) {
            int index = colors.indexOf(pixels[i]);
            paletteIndices[i] = (byte) index;
        }
    }

    // doesn't take osrs definition into account
    public boolean isSame(RSTexture other) {
        if (innerWidth != other.innerWidth) {
            System.out.println("Width is not the same | " + innerWidth + " != " + other.innerWidth);
            return false;
        }

        if (innerHeight != other.innerHeight) {
            System.out.println("Height is not the same | " + innerHeight + " != " + other.innerHeight);
            return false;
        }

        if (xOffset != other.xOffset) {
            System.out.println("X offset is not the same | " + xOffset + " != " + other.xOffset);
            return false;
        }

        if (yOffset != other.yOffset) {
            System.out.println("Y offset is not the same | " + yOffset + " != " + other.yOffset);
            return false;
        }

        if (width != other.width) {
            System.out.println("Crop W is not the same | " + width + " != " + other.width);
            return false;
        }

        if (height != other.height) {
            System.out.println("Crop H is not the same | " + height + " != " + other.height);
            return false;
        }

        if (!Arrays.equals(pixels, other.pixels)) {
            System.out.println("Pixels are not the same");
            return false;
        }

        if (!Arrays.equals(palette, other.palette)) {
            System.out.println("Palette is not the same");
            return false;
        }

        if (!Arrays.equals(paletteIndices, other.paletteIndices)) {
            System.out.println("Palette indices are not the same");
            return false;
        }

        if (!Arrays.equals(alphas, other.alphas)) {
            System.out.println("Alphas are not the same");
            return false;
        }

        return true;
    }

    public int getUniqueColorCount() {
        return (int) Arrays.stream(pixels).distinct().count();
    }

    public RSTexture(BufferedImage bufferedImage) {
        this.pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        this.innerWidth = bufferedImage.getWidth();
        this.innerHeight = bufferedImage.getHeight();
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
    }

    public RSTexture(int innerWidth, int innerHeight) {
        this.pixels = new int[innerWidth * innerHeight];
        this.innerWidth = this.width = innerWidth;
        this.innerHeight = this.height = innerHeight;
    }

    public RSTexture(int innerWidth, int innerHeight, int[] pixels) {
        this.innerWidth = this.width = innerWidth;
        this.innerHeight = this.height = innerHeight;
        this.pixels = pixels;
    }

    public RSTexture(int width, int height, int horizontalOffset, int verticalOffset, int innerWidth, int innerHeight, int flags,
                     int[] pixels) {
        this.width = width;
        this.height = height;
        this.xOffset = horizontalOffset;
        this.yOffset = verticalOffset;
        this.innerWidth = innerWidth;
        this.innerHeight = innerHeight;
        this.flags = flags;
        this.pixels = pixels;
    }

    public void trim() {
        if (this.innerWidth == this.width && this.innerHeight == this.height) {
            return;
        }
        int[] pixels = new int[this.width * this.height];
        System.out.println(this.width + ", " + this.height + ", " + this.innerWidth + ", " + this.innerHeight);
        for (int y = 0; y < this.innerHeight; y++) {
            for (int x = 0; x < this.innerWidth; x++) {
                pixels[(y + this.yOffset) * this.width + x + this.xOffset] = this.pixels[y * this.innerWidth + x];
            }
        }
        this.pixels = pixels;
        this.innerWidth = this.width;
        this.innerHeight = this.height;
        this.xOffset = 0;
        this.yOffset = 0;
    }

    public BufferedImage toBufferedImage() {

        BufferedImage image = new BufferedImage(this.innerWidth, this.innerHeight, BufferedImage.TYPE_INT_RGB);

        final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(this.pixels, 0, pixels, 0, this.pixels.length);

        return image;
    }

    public BufferedImage toBufferedImageARGB() {
        BufferedImage image = new BufferedImage(this.innerWidth, this.innerHeight, BufferedImage.TYPE_INT_ARGB);

        final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(this.pixels, 0, pixels, 0, this.pixels.length);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xFF000000 | pixels[i];

            if ((pixels[i] & 0xFFFFFF) == 0) {
                pixels[i] = 0;
            }
        }

        return image;
    }

    public void outline(int color) {
        int[] outlinedPixels = new int[innerWidth * innerHeight];
        int start = 0;
        for (int y = 0; y < innerHeight; y++) {
            for (int x = 0; x < innerWidth; x++) {
                int outline = pixels[start];
                if (outline == 0) {
                    if (x > 0 && pixels[start - 1] != 0) {
                        outline = color;
                    } else if (y > 0 && pixels[start - innerWidth] != 0) {
                        outline = color;
                    } else if (x < innerWidth - 1 && pixels[start + 1] != 0) {
                        outline = color;
                    } else if (y < innerHeight - 1 && pixels[start + innerWidth] != 0) {
                        outline = color;
                    }
                }
                outlinedPixels[start++] = outline;
            }
        }
        pixels = outlinedPixels;
    }

    public void shadow(int color) {
        for (int y = innerHeight - 1; y > 0; y--) {
            int pos = y * innerWidth;
            for (int x = innerWidth - 1; x > 0; x--) {
                if (pixels[x + pos] == 0 && pixels[x + pos - 1 - innerWidth] != 0) {
                    pixels[x + pos] = color;
                }
            }
        }
    }

}
