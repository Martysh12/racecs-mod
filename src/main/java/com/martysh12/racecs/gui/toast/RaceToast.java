package com.martysh12.racecs.gui.toast;

import com.martysh12.racecs.RaceCS;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RaceToast implements Toast {
    private static final Identifier TEXTURE = new Identifier(RaceCS.MOD_ID, "textures/gui/race_toasts.png");
    private static final int TEXT_START_X = 7;

    private Background background;
    private Icon icon;
    private TitleColor titleColor;

    private Text title;
    private Text description;

    private boolean propertiesUpdated;
    private long startTime;
    private int toastWidth;

    public RaceToast(Text title, Text description, Background toastColor, Icon toastIcon, TitleColor titleColor) {
        setTitle(title);
        setDescription(description);
        setToastColor(toastColor);
        setToastIcon(toastIcon);
        setTitleColor(titleColor);
    }

    public void setTitle(Text title) {
        this.title = title;
        propertiesUpdated = true;
    }

    public void setDescription(Text description) {
        this.description = description;
        int realToastWidth = TEXT_START_X + RaceCS.mc.textRenderer.getWidth(description) + Icon.ICON_WIDTH;
        toastWidth = (int) (32 * Math.ceil(realToastWidth / 32.0));
        propertiesUpdated = true;
    }

    public void setToastColor(Background background) {
        this.background = background;
        propertiesUpdated = true;
    }

    public void setToastIcon(Icon icon) {
        this.icon = icon;
        propertiesUpdated = true;
    }

    public void setTitleColor(TitleColor titleColor) {
        this.titleColor = titleColor;
        propertiesUpdated = true;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long time) {
        // Reset the timer whenever there is an update
        if (propertiesUpdated) {
            this.startTime = time;
            propertiesUpdated = false;
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);

        background.drawBackground(matrices, manager, toastWidth);
        icon.drawIcon(matrices, manager, toastWidth);

        RaceCS.mc.textRenderer.draw(matrices, title, 7, 7, titleColor.getColor());
        RaceCS.mc.textRenderer.draw(matrices, description, 7, 18, 0xFFFFFFFF);

        return time - this.startTime < 5000 ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public int getWidth() {
        return toastWidth;
    }

    @Override
    public int getHeight() {
        return Background.BACKGROUND_HEIGHT;
    }

    public enum Background {
        GREEN(0),
        RED(1),
        YELLOW(2),
        BLUE(3);

        private static final int BACKGROUND_WIDTH = 160;
        private static final int BACKGROUND_HEIGHT = 32;

        private final int textureSlotY;

        Background(int textureSlotY) {
            this.textureSlotY = textureSlotY;
        }

        private void drawBackground(MatrixStack matrices, DrawableHelper helper, int toastWidth) {
            if (toastWidth == BACKGROUND_WIDTH) {
                helper.drawTexture(matrices, 0, 0, 0, textureSlotY * 32, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
            } else {
                // Oh, no
                helper.drawTexture(matrices, 0, 0, 0, textureSlotY * 32, 32, BACKGROUND_HEIGHT);

                for (int i = 32; i < (toastWidth - Icon.ICON_WIDTH); i += 32) {
                    helper.drawTexture(matrices, i, 0, 32, textureSlotY * 32, 32, BACKGROUND_HEIGHT);
                }

                helper.drawTexture(matrices, toastWidth - Icon.ICON_WIDTH, 0, BACKGROUND_WIDTH - Icon.ICON_WIDTH, textureSlotY * 32, 32, BACKGROUND_HEIGHT);
            }
        }
    }

    public enum Icon {
        ARRIVAL(0),
        COLLISION(1),
        TROPHY(2),
        CHECKMARK(3),
        FIRST(4),
        ARRIVAL_PLAYER(5),
        TEAM_PARTIAL_COMPLETION(6);

        private static final int ICON_WIDTH = 32;
        private static final int ICON_HEIGHT = 32;

        private final int textureSlotY;

        Icon(int textureSlotY) {
            this.textureSlotY = textureSlotY;
        }

        public void drawIcon(MatrixStack matrices, DrawableHelper helper, int toastWidth) {
            helper.drawTexture(matrices, toastWidth - ICON_WIDTH, 0, 224, textureSlotY * ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
        }
    }

    public enum TitleColor {
        RED(0xFF1818),
        YELLOW(0xFFFF00),
        GREEN(0x00FF00);

        private final int color;

        TitleColor(int color) {
            this.color = color;
        }

        public int getColor() {
            return 0xFF000000 | color;
        }

        public int getColor(int alpha) {
            return (alpha << 24) | color;
        }
    }
}
