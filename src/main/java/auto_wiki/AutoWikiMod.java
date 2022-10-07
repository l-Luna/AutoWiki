package auto_wiki;

import auto_wiki.render.ItemStackRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AutoWikiMod implements ModInitializer{

	public void onInitialize(ModContainer mod){
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, error) -> {
			doGenerate();
		});
		HudRenderCallback.EVENT.register(new ItemStackRenderer());
	}

	private static void doGenerate(){
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
			// TODO: check creative tab for variants (e.g. potions...)
			Path target = QuiltLoader.getGameDir()
					.resolve("auto_wiki_textures")
					.resolve(identifier.getNamespace())
					.resolve(identifier.getPath() + ".png");
			if(!target.toFile().exists())
				ItemStackRenderer.RENDER_QUEUE.add(new ItemStack(item));
		}
	}
}