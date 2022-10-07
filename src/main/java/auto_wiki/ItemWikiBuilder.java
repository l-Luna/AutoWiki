package auto_wiki;

import auto_wiki.layout.*;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemWikiBuilder{

	private static final Map<TagKey<Block>, String> mineableTags = Map.of(
			BlockTags.AXE_MINEABLE, "Axe",
			BlockTags.HOE_MINEABLE, "Hoe",
			BlockTags.PICKAXE_MINEABLE, "Pickaxe",
			BlockTags.SHOVEL_MINEABLE, "Shovel",
			FabricMineableTags.SWORD_MINEABLE, "Sword",
			FabricMineableTags.SHEARS_MINEABLE, "Shears"
	);

	private static final Map<Integer, String> miningLevelNames = Map.of(
			-1, "Any (-1)",
			0, "Hand (0)",
			1, "Stone (1)",
			2, "Iron (2)",
			3, "Diamond (3)",
			4, "Netherite (4)"
	);

	public static WikiPage createFor(Item item){
		WikiPage page = new WikiPage();
		page.append(new TextParagraph("# " + item.getName().getString()));
		page.append(icon(item, 100));
		page.append(infoTable(item));
		if(item instanceof BlockItem block)
			page.append(blockInfoTable(block));
		return page;
	}

	public static Paragraph icon(Item item, int size){
		Identifier id = Registry.ITEM.getId(item);
		return new ImageParagraph("../../auto_wiki_textures/" + id.getNamespace() + "/" + id.getPath() + ".png", "item icon", size);
	}

	private static Paragraph infoTable(Item item){
		List<String> headers = new ArrayList<>(List.of("Item properties", "Id", "Max stack size"));
		List<String> values = new ArrayList<>(List.of("", formatId(item), String.valueOf(item.getMaxCount())));
		if(item.isDamageable()){
			headers.add("Durability");
			values.add(String.valueOf(item.getMaxDamage()));
		}
		for(EquipmentSlot value : EquipmentSlot.values()){
			var modifiers = item.getAttributeModifiers(value);
			List<String> effects = new ArrayList<>();
			for(Map.Entry<EntityAttribute, EntityAttributeModifier> modifier : modifiers.entries())
				effects.add(formatModifier(modifier.getKey(), modifier.getValue()));
			if(effects.size() > 0){
				headers.add(I18n.translate("item.modifiers." + value.getName()));
				values.add(String.join("<br>", effects));
			}
		}
		var tags = item.getBuiltInRegistryHolder().streamTags().toList();
		if(tags.size() > 0){
			headers.add("Tags");
			values.add(formatTags(tags.stream().map(TagKey::id)));
		}
		return new TableParagraph(List.of(headers, values));
	}

	private static Paragraph blockInfoTable(BlockItem blockItem){
		Block block = blockItem.getBlock();
		List<String> headers = new ArrayList<>(List.of("Block properties", "Id"));
		List<String> values = new ArrayList<>(List.of("", formatId(block)));

		if(block.getHardness() == -1){
			headers.add("Hardness/resistance");
			values.add("Unbreakable");
		}else if(block.getHardness() != block.getBlastResistance()){
			headers.addAll(List.of("Hardness", "Resistance"));
			values.addAll(List.of(String.valueOf(block.getHardness()), String.valueOf(block.getBlastResistance())));
		}else{
			headers.add("Hardness/resistance");
			values.add(String.valueOf(block.getHardness()));
		}

		var state = block.getDefaultState();
		int miningLevel = MiningLevelManager.getRequiredMiningLevel(state);
		if(miningLevel > 0 || state.isToolRequired()){
			headers.add("Mining level");
			values.add(miningLevelNames.getOrDefault(miningLevel, String.valueOf(miningLevel)));
		}

		var tags = block.getBuiltInRegistryHolder().streamTags().toList();
		if(tags.size() > 0){
			// TODO: what if a block has multiple mineable tags?
			for(var entry : mineableTags.entrySet()){
				if(tags.contains(entry.getKey())){
					String text = entry.getValue();
					if(state.isToolRequired())
						text += " (Required)";
					headers.add("Tool");
					values.add(text);
					break;
				}
			}

			headers.add("Tags");
			values.add(formatTags(tags.stream().map(TagKey::id)));
		}

		return new TableParagraph(List.of(headers, values));
	}

	@NotNull
	private static String formatTags(Stream<Identifier> tags){
		return tags.map(Identifier::toString)
				.map(x -> "`" + x + "`")
				.collect(Collectors.joining("<br>"));
	}

	private static String formatModifier(EntityAttribute key, EntityAttributeModifier modifier){
		double amount;
		if(modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || modifier.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
			amount = modifier.getValue() * 100;
		else
			amount = modifier.getValue();

		if(modifier.getValue() >= 0)
			return I18n.translate(
					"attribute.modifier.plus." + modifier.getOperation().getId(),
					ItemStack.MODIFIER_FORMAT.format(amount),
					I18n.translate(key.getTranslationKey())
			);
		else return I18n.translate(
				"attribute.modifier.take." + modifier.getOperation().getId(),
				ItemStack.MODIFIER_FORMAT.format(-amount),
				I18n.translate(key.getTranslationKey())
		);
	}

	private static String formatId(Item block){
		return "`" + Registry.ITEM.getId(block) + "`";
	}

	private static String formatId(Block block){
		return "`" + Registry.BLOCK.getId(block) + "`";
	}
}