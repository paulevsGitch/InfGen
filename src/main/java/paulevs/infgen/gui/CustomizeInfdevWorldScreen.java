package paulevs.infgen.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import paulevs.infgen.InfGen;

public class CustomizeInfdevWorldScreen extends Screen
{
	private CreateWorldScreen parent;
	private CompoundTag generatorOptions;
	private boolean onlyInfdevBiome = false;
	private int biomeSize = 3;
	
	private boolean hasVillages = true;
	private boolean hasDungeons = true;
	private boolean hasMineshafts = true;
	private boolean hasOtherStructures = true;

	private ButtonListWidget list;

	public CustomizeInfdevWorldScreen(CreateWorldScreen parent, CompoundTag generatorOptions)
	{
		super(new TranslatableText("createWorld.customize.flat.title", new Object[0]));
		this.parent = parent;
		this.generatorOptions = generatorOptions;
		this.parent.generatorOptionsTag = generatorOptions;

		if (generatorOptions.contains("only_infdev_biome"))
			onlyInfdevBiome = generatorOptions.getBoolean("only_infdev_biome");
		if (generatorOptions.contains("biome_size"))
			biomeSize = generatorOptions.getInt("biome_size");
		
		if (generatorOptions.contains("has_villages"))
			hasVillages = generatorOptions.getBoolean("has_villages");
		if (generatorOptions.contains("has_dungeons"))
			hasDungeons = generatorOptions.getBoolean("has_dungeons");
		if (generatorOptions.contains("has_mineshafts"))
			hasMineshafts = generatorOptions.getBoolean("has_mineshafts");
		if (generatorOptions.contains("has_other_structures"))
			hasOtherStructures = generatorOptions.getBoolean("has_other_structures");
	}

	@Override
	protected void init()
	{
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));

		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
			openScreenAndEnableButton();
		}));

		BooleanOption switchVillages = new BooleanOption(
				"options.infgen.has_villages",
				(gameOptions) -> {
					return hasVillages;
				}, (gameOptions, value) -> {
					hasVillages = value;
					generatorOptions.putBoolean("has_villages", hasVillages);
				});
		
		BooleanOption switchDungeons = new BooleanOption(
				"options.infgen.has_dungeons",
				(gameOptions) -> {
					return hasDungeons;
				}, (gameOptions, value) -> {
					hasDungeons = value;
					generatorOptions.putBoolean("has_dungeons", hasDungeons);
				});
		
		BooleanOption switchMineshafts = new BooleanOption(
				"options.infgen.has_mineshafts",
				(gameOptions) -> {
					return hasMineshafts;
				}, (gameOptions, value) -> {
					hasMineshafts = value;
					generatorOptions.putBoolean("has_mineshafts", hasMineshafts);
				});
		
		BooleanOption switchOther = new BooleanOption(
				"options.infgen.has_other_structures",
				(gameOptions) -> {
					return hasOtherStructures;
				}, (gameOptions, value) -> {
					hasOtherStructures = value;
					generatorOptions.putBoolean("has_other_structures", hasOtherStructures);
				});
		
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

		this.list.addSingleOptionEntry(new BooleanOption(
				"options.infgen.only_infdev_biome",
				(gameOptions) -> {
					return onlyInfdevBiome;
				}, (gameOptions, value) -> {
					onlyInfdevBiome = value;
					generatorOptions.putBoolean("only_infdev_biome", onlyInfdevBiome);
				}));
		
		this.list.addSingleOptionEntry(new DoubleOption("options.infgen.biome_size", 1.0D, 16.0D, 1.0F, (gameOptions) -> {
			return (double) biomeSize;
		}, (gameOptions, value) -> {
			biomeSize = (int) Math.round(value);
			generatorOptions.putInt("biome_size", biomeSize);
		},(gameOptions, doubleOption) -> {
			return String.format("%s: %d",
					I18n.translate("options.infgen.biome_size"),
					biomeSize);
		}));
		
		this.list.addSingleOptionEntry(switchVillages);
		this.list.addSingleOptionEntry(switchDungeons);
		this.list.addSingleOptionEntry(switchMineshafts);
		this.list.addSingleOptionEntry(switchOther);
		
		this.children.add(this.list);
	}

	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}

	protected void openScreenAndEnableButton()
	{
		this.minecraft.openScreen(this.parent);
		if (InfGen.button_customize != null)
			InfGen.button_customize.visible = true;
	}
}