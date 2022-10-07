package auto_wiki.render;

import com.mojang.blaze3d.framebuffer.Framebuffer;
import com.mojang.blaze3d.framebuffer.SimpleFramebuffer;
import com.mojang.blaze3d.lighting.DiffuseLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.texture.NativeImage;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import org.lwjgl.opengl.GL12;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

// from RebornCore, used under MIT
// from https://github.com/TechReborn/TechReborn/blob/1.19/RebornCore/src/client/java/reborncore/client/ItemStackRenderer.java
// with minor changes for quilt and auto-wiki
public class ItemStackRenderer implements HudRenderCallback{

	public static final Queue<ItemStack> RENDER_QUEUE = new LinkedList<>();

	private static final int SIZE = 512;

	@Override
	public void onHudRender(MatrixStack matrixStack, float v){
		if(!RENDER_QUEUE.isEmpty()){
			ItemStack itemStack = RENDER_QUEUE.remove();
			Identifier id = Registry.ITEM.getId(itemStack.getItem());
			MinecraftClient.getInstance().textRenderer.draw(matrixStack, "Rendering " + id + ", " + RENDER_QUEUE.size() + " items left", 5, 5, -1);
			export(id, itemStack);
		}
	}

	private void export(Identifier identifier, ItemStack item){
		RenderSystem.setProjectionMatrix(Matrix4f.projectionMatrix(0, 16, 0, 16, 1000, 3000));
		MatrixStack stack = RenderSystem.getModelViewStack();
		stack.loadIdentity();
		stack.translate(0, 0, -2000);
		DiffuseLighting.setup3DGuiLighting();
		RenderSystem.applyModelViewMatrix();

		Framebuffer framebuffer = new SimpleFramebuffer(SIZE, SIZE, true, MinecraftClient.IS_SYSTEM_MAC);

		try(NativeImage nativeImage = new NativeImage(SIZE, SIZE, true)){
			framebuffer.setClearColor(0, 0, 0, 0);
			framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

			framebuffer.beginWrite(true);
			GlStateManager._clear(GL12.GL_COLOR_BUFFER_BIT | GL12.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
			MinecraftClient.getInstance().getItemRenderer().renderInGui(item, 0, 0);
			framebuffer.endWrite();

			framebuffer.bindColorAttachmentAsTexture();
			nativeImage.loadFromTextureImage(0, false);
			nativeImage.mirrorVertically();
			framebuffer.endRead();

			try{
				Path path = QuiltLoader.getGameDir()
						.resolve("auto_wiki_textures")
						.resolve(identifier.getNamespace())
						.resolve(identifier.getPath() + ".png");
				Files.createDirectories(path.getParent());
				nativeImage.writeFile(path);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		framebuffer.delete();
	}
}