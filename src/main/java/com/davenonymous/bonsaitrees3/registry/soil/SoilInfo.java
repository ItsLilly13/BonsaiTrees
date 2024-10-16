package com.davenonymous.bonsaitrees3.registry.soil;

import com.davenonymous.libnonymous.base.RecipeData;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SoilInfo extends RecipeData {

	public enum SoilType {
		BLOCK,
		FLUID,
		TEXTURE
	}

	private final ResourceLocation id;
	public Ingredient ingredient;
	public BlockState blockState;
	public FluidState fluidState;
	public ResourceLocation soilTexture;
	public float tickModifier;
	public Set<String> tags;
	public SoilType soilType;

	public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState blockState, float tickModifier) {
		this.id = id;
		this.ingredient = ingredient;
		this.blockState = blockState;
		this.tickModifier = tickModifier;
		this.tags = new HashSet<>();
		this.soilType = SoilType.BLOCK;
	}

	public SoilInfo(ResourceLocation id, Ingredient ingredient, FluidState fluidState, float tickModifier) {
		this.id = id;
		this.ingredient = ingredient;
		this.fluidState = fluidState;
		this.tickModifier = tickModifier;
		this.tags = new HashSet<>();
		this.soilType = SoilType.FLUID;
	}

	public SoilInfo(ResourceLocation id, Ingredient ingredient, ResourceLocation soilTexture, float tickModifier) {
		this.id = id;
		this.ingredient = ingredient;
		this.soilTexture = soilTexture;
		this.tickModifier = tickModifier;
		this.tags = new HashSet<>();
		this.soilType = SoilType.TEXTURE;
	}

	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public boolean isValidTag(String tag) {
		return this.tags.contains(tag);
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.SOIL_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return Registration.RECIPE_TYPE_SOIL.get();
	}

	public float getTickModifier() {
		return tickModifier;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SoilInfo other && other.soilType == this.soilType && other.id.equals(this.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, soilType);
	}
}