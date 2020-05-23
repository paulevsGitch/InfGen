package paulevs.infgen.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import paulevs.infgen.generator.SurfaceBiomes;

public class InfdevBiomesScreen extends Screen
{
	private CustomizeInfdevWorldScreen parent;
	private Map<Biome, Boolean> biomes;

	private ButtonListWidget list;
	private CompoundTag generatorOptions;

	public InfdevBiomesScreen(CustomizeInfdevWorldScreen parent, CompoundTag generatorOptions)
	{
		super(new TranslatableText("createWorld.customize.flat.title", new Object[0]));
		this.parent = parent;
		this.generatorOptions = generatorOptions;

		if (generatorOptions.contains("biomes"))
			biomes = parceBiomes(generatorOptions.getByteArray("biomes"));
		else
			biomes = initBiomes();
	}

	@Override
	protected void init()
	{
		ArrayList<BooleanOption> options = new ArrayList<BooleanOption>();
		
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), (buttonWidget) -> {
			openParrent();
		}));

		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
			openParrent();
		}));
		
		this.addButton(new ButtonWidget(this.width / 2 - 155, 8, 150, 20, I18n.translate("options.infgen.enable_all"), (buttonWidget) -> {
			for (Biome b: biomes.keySet())
				biomes.put(b, true);
			this.list.children().forEach((entry) -> {
				entry.children().forEach((element) -> {
					if (element instanceof AbstractButtonWidget)
					{
						AbstractButtonWidget button = (AbstractButtonWidget) element;
						String start = button.getMessage();
						start = start.substring(0, start.lastIndexOf(":"));
						button.setMessage(start + ": §a" + I18n.translate("options.on"));
					}
				});
			});
		}));
		
		this.addButton(new ButtonWidget(this.width / 2 + 5, 8, 150, 20, I18n.translate("options.infgen.disable_all"), (buttonWidget) -> {
			for (Biome b: biomes.keySet())
				biomes.put(b, false);
			this.list.children().forEach((entry) -> {
				entry.children().forEach((element) -> {
					if (element instanceof AbstractButtonWidget)
					{
						AbstractButtonWidget button = (AbstractButtonWidget) element;
						String start = button.getMessage();
						start = start.substring(0, start.lastIndexOf(":"));
						button.setMessage(start + ": §c" + I18n.translate("options.off"));
					}
				});
			});
		}));
		
		this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		ArrayList<Biome> biomesList = new ArrayList<Biome>();
		biomesList.addAll(biomes.keySet());
		biomesList.sort(new Comparator<Biome>() {
			@Override
			public int compare(Biome biome1, Biome biome2)
			{
				String name1 = I18n.translate(biome1.getTranslationKey());
				String name2 = I18n.translate(biome2.getTranslationKey());
				return name1.compareTo(name2);
			}
		});
		
		biomesList.forEach((biome) -> {
			BooleanOption option = new BooleanOption(
					"options.infgen." + biome.getTranslationKey(),
					(gameOptions) -> {
						return biomes.get(biome);
					}, (gameOptions, value) -> {
						biomes.put(biome, value);
					})
			{
				@Override
				public String getDisplayString(GameOptions options)
				{
					boolean enable = this.get(options);
					String sep = enable ? ": §a" : ": §c";
					return I18n.translate(biome.getTranslationKey()) + sep + I18n.translate(enable ? "options.on" : "options.off");
				}
			};
		
			this.list.addSingleOptionEntry(option);
			options.add(option);
		});
		
		this.children.add(this.list);
	}
	
	private void openParrent()
	{
		generatorOptions.putByteArray("biomes", getBiomes());
		this.minecraft.openScreen(this.parent);
	}

	public void render(int mouseX, int mouseY, float delta)
	{
		this.renderBackground();
		this.list.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
	
	private static Map<Biome, Boolean> parceBiomes(byte[] buffer)
	{
		Map<Biome, Boolean> result = new HashMap<Biome, Boolean>();
		Set<Biome> available = new HashSet<Biome>();
		String biomesAll = new String(buffer);
		String[] biomes = biomesAll.split(";");
		for (String biome: biomes)
		{
			Identifier id = new Identifier(biome);
			if (Registry.BIOME.containsId(id))
			{
				available.add(Registry.BIOME.get(id));
			}
		}
		for (Biome biome: SurfaceBiomes.BIOMES)
		{
			result.put(biome, available.contains(biome));
		}
		return result;
	}
	
	private Map<Biome, Boolean> initBiomes()
	{
		Map<Biome, Boolean> result = new HashMap<Biome, Boolean>();
		for (Biome biome: SurfaceBiomes.BIOMES)
		{
			result.put(biome, true);
		}
		return result;
	}
	
	private byte[] getBiomes()
	{
		String result = "";
		for (Biome biome: biomes.keySet())
		{
			if (biomes.get(biome))
			{
				String name = Registry.BIOME.getId(biome).toString();
				result += result.isEmpty() ? name : ";" + name;
			}	
		}
		return result.getBytes();
	}
}