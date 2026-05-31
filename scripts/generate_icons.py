"""
Generates ic_launcher.png at all required Android mipmap densities + 512x512 for Play Store.
Design: deep-blue background, 4x4 grid of rounded-tile puzzle pieces, bottom-right tile empty.
"""

from PIL import Image, ImageDraw, ImageFont
import os

# ── Output sizes ────────────────────────────────────────────────────────────
SIZES = {
    "mipmap-mdpi":     48,
    "mipmap-hdpi":     72,
    "mipmap-xhdpi":    96,
    "mipmap-xxhdpi":   144,
    "mipmap-xxxhdpi":  192,
}
STORE_SIZE = 512  # Play Store high-res icon

# ── Colors ───────────────────────────────────────────────────────────────────
BG_COLOR      = (21,  101, 192)   # #1565C0  (Material Blue 800)
TILE_COLOR    = (255, 255, 255)   # white tiles
TILE_SHADOW   = (13,  71,  161)   # #0D47A1  (Blue 900) — subtle border
EMPTY_COLOR   = (13,  71,  161)   # same as shadow — sunken empty slot
NUM_COLOR     = (21, 101, 192)    # number text matches background

RES_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")


def draw_icon(size: int) -> Image.Image:
    img = Image.new("RGBA", (size, size), BG_COLOR)
    draw = ImageDraw.Draw(img)

    grid       = 2          # 2×2 grid shape
    padding    = size * 0.12
    gap        = size * 0.06
    tile_size  = (size - 2 * padding - (grid - 1) * gap) / grid
    radius     = max(2, tile_size * 0.18)

    for row in range(grid):
        for col in range(grid):
            is_empty = (row == grid - 1 and col == grid - 1)  # bottom-right

            x0 = padding + col * (tile_size + gap)
            y0 = padding + row * (tile_size + gap)
            x1 = x0 + tile_size
            y1 = y0 + tile_size

            if is_empty:
                # Draw sunken slot
                draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=EMPTY_COLOR)
            else:
                # Shadow (slightly offset)
                offset = max(1, size * 0.008)
                draw.rounded_rectangle(
                    [x0 + offset, y0 + offset, x1 + offset, y1 + offset],
                    radius=radius, fill=TILE_SHADOW
                )
                # Tile
                draw.rounded_rectangle([x0, y0, x1, y1], radius=radius, fill=TILE_COLOR)

    return img


def main():
    # Generate mipmap PNGs
    for folder, size in SIZES.items():
        out_dir = os.path.join(RES_DIR, folder)
        os.makedirs(out_dir, exist_ok=True)
        path = os.path.join(out_dir, "ic_launcher.png")
        draw_icon(size).save(path, "PNG")
        print(f"  {size}x{size}  → {path}")

    # Generate 512×512 Play Store icon
    store_dir = os.path.join(os.path.dirname(__file__), "..", "store_assets")
    os.makedirs(store_dir, exist_ok=True)
    store_path = os.path.join(store_dir, "ic_launcher_512.png")
    draw_icon(STORE_SIZE).convert("RGB").save(store_path, "PNG")
    print(f"\n  {STORE_SIZE}x{STORE_SIZE} (Play Store) → {store_path}")


if __name__ == "__main__":
    print("Generating icons...")
    main()
    print("\nDone.")
