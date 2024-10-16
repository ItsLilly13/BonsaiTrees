package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees3.config.ClientConfig;
import com.davenonymous.libnonymous.render.MultiBlockBlockAndTintGetter;
import com.davenonymous.libnonymous.render.MultiBlockBlockColors;
import com.davenonymous.libnonymous.render.MultiblockBakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class BonsaiPotRenderer implements BlockEntityRenderer<BonsaiPotBlockEntity> {
	public static final ResourceLocation WATER = new ResourceLocation("minecraft", "block/water_still");

	public BonsaiPotRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(BonsaiPotBlockEntity pPotBlock, float pPartialTick, PoseStack poseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		if(!pPotBlock.hasSapling() || !pPotBlock.hasSoil()) {
			return;
		}

		if(pPotBlock.getLevel() == null) {
			return;
		}

		var saplingInfo = pPotBlock.getSaplingInfo();
		if(saplingInfo == null) {
			return;
		}

		var renderItemInstead = ClientConfig.alwaysRenderAsItem.get();
		var multiBlock = TreeModels.get(saplingInfo.getId());
		if(multiBlock == null) {
			renderItemInstead = true;
		}

		float scaleRatio = 1.0f;
		if(!renderItemInstead) {
			scaleRatio = (float) multiBlock.getScaleRatio(true);
		}

		poseStack.pushPose();
		poseStack.translate(0.5f, 0.1f, 0.5f);

		poseStack.scale(scaleRatio, scaleRatio, scaleRatio);

		float maxSize = 0.9f;
		poseStack.scale(maxSize, maxSize, maxSize);

		float progress = (float) pPotBlock.getProgress(pPartialTick);
		poseStack.scale(progress, progress, progress);

		var rendertype = RenderType.cutout();
		var mc = Minecraft.getInstance();
		if(mc.options.graphicsMode().get().getId() == GraphicsStatus.FAST.getId()) {
			rendertype = RenderType.solid();
		}

		if(renderItemInstead) {
			if(saplingInfo.ingredient != null && saplingInfo.ingredient.getItems().length > 0) {
				Block blockToRender = Block.byItem(saplingInfo.ingredient.getItems()[0].getItem());
				if(blockToRender != Blocks.AIR) {
					poseStack.scale(maxSize, maxSize, maxSize);
					poseStack.translate(-0.5f, 0.0f, -0.5f);
					mc.getBlockRenderer().renderSingleBlock(blockToRender.defaultBlockState(), poseStack, pBufferSource, pPackedLight, pPackedOverlay, ModelData.EMPTY, rendertype);
				}
			}
		} else {
			double rotate = Math.toRadians(pPotBlock.modelRotation * 90.0f);
			poseStack.mulPose(new Quaternionf(new AxisAngle4f((float)rotate, 0, 1, 0)));

			float translateOffsetX = (float) (multiBlock.width + 1) / 2.0f;
			float translateOffsetY = 0.0f;
			float translateOffsetZ = (float) (multiBlock.depth + 1) / 2.0f;
			poseStack.translate(-translateOffsetX, -translateOffsetY, -translateOffsetZ);

			var buffer = pBufferSource.getBuffer(rendertype);
			var baked = MultiblockBakedModel.of(multiBlock);

			MultiBlockBlockAndTintGetter fakeLevel = new MultiBlockBlockAndTintGetter(multiBlock, pPotBlock.getLevel(), pPotBlock.getBlockPos());
			var mr = new TreeModelRenderer(new MultiBlockBlockColors(multiBlock));

			if(mc.options.graphicsMode().get().getId() >= GraphicsStatus.FANCY.getId()) {
				mr.tesselateWithAO(fakeLevel, baked, pPotBlock.getBlockState(), pPotBlock.getBlockPos(), poseStack, buffer, true, pPotBlock.getLevel().random, 0, pPackedLight, ModelData.EMPTY, rendertype);
			} else {
				mr.tesselateWithoutAO(fakeLevel, baked, pPotBlock.getBlockState(), pPotBlock.getBlockPos(), poseStack, buffer, true, pPotBlock.getLevel().random, 0, pPackedLight, ModelData.EMPTY, rendertype);
			}
		}

		poseStack.popPose();
	}
}