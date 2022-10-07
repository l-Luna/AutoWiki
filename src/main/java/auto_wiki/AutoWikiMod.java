package auto_wiki;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AutoWikiMod implements ModInitializer{

	public void onInitialize(ModContainer mod){
		for(Item item : Registry.ITEM){
			String markdown = ItemWikiBuilder.createFor(item).toMarkdown();
			Identifier identifier = Registry.ITEM.getId(item);
			String path = "./auto_wiki/" + identifier.getNamespace() + "/" + identifier.getPath() + ".md";
			Path asPath = Path.of(path).normalize().toAbsolutePath();
			try{
				asPath.getParent().toFile().mkdirs();
				Files.writeString(asPath, markdown, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
		System.exit(0);
	}
}