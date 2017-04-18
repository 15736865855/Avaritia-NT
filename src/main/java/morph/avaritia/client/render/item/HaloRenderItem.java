package morph.avaritia.client.render.item;

import codechicken.lib.colour.Colour;
import morph.avaritia.api.IHaloRenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.IModelState;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by covers1624 on 14/04/2017.I
 */
public class HaloRenderItem extends WrappedItemRenderer {

    private Random randy = new Random();

    public HaloRenderItem(IModelState state, IBakedModel model) {
        super(state, model);
    }

    public HaloRenderItem(IModelState state, IWrappedModelGetter getter) {
        super(state, getter);
    }

    @Override
    public void renderItem(ItemStack stack) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        if (stack.getItem() instanceof IHaloRenderItem && transformType == TransformType.GUI) {
            IHaloRenderItem hri = ((IHaloRenderItem) stack.getItem());

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            //RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();

            if (hri.shouldDrawHalo(stack)) {
                Colour.glColourARGB(hri.getHaloColour(stack));
                TextureAtlasSprite sprite = hri.getHaloTexture(stack);

                double spread = hri.getHaloSize(stack) / 16D;
                double min = 0D - spread;
                double max = 1D + spread;

                float minU = sprite.getMinU();
                float maxU = sprite.getMaxU();
                float minV = sprite.getMinV();
                float maxV = sprite.getMaxV();

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);

                buffer.pos(max, max, 0).tex(maxU, minV).endVertex();
                buffer.pos(min, max, 0).tex(minU, minV).endVertex();
                buffer.pos(min, min, 0).tex(minU, maxV).endVertex();
                buffer.pos(max, min, 0).tex(maxU, maxV).endVertex();

                tess.draw();
            }

            if (hri.shouldDrawPulse(stack)) {
                GlStateManager.pushMatrix();
                double scale = randy.nextDouble() * 0.15 + 0.95;
                double trans = (1 - scale) / 2;
                GlStateManager.translate(trans, trans, 0);
                GlStateManager.scale(scale, scale, 1.0);

                renderModel(wrapped, stack, 0.6F);

                GlStateManager.popMatrix();
            }

            renderModel(wrapped, stack);

            GlStateManager.enableAlpha();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableDepth();

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } else {
            renderModel(wrapped, stack);
        }
    }
}
