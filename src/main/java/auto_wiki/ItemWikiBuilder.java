package auto_wiki;

import auto_wiki.layout.Element;
import auto_wiki.layout.TableElement;
import auto_wiki.layout.TextElement;
import auto_wiki.layout.WikiPage;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ItemWikiBuilder{

	public static WikiPage createFor(Item item){
		WikiPage page = new WikiPage();
		page.append(new TextElement("# " + item.getName().getString()));
		page.append(infoTable(item));
		return page;
	}

	private static Element infoTable(Item item){
		List<String> headers = new ArrayList<>(List.of("Id", "Max stack size"));
		List<String> values = new ArrayList<>(List.of(Registry.ITEM.getId(item).toString(), String.valueOf(item.getMaxCount())));
		if(item.isDamageable()){
			headers.add("Durability");
			values.add(String.valueOf(item.getMaxDamage()));
		}
		return new TableElement(List.of(headers, values));
	}
}