package auto_wiki.layout;

public class ImageParagraph implements Paragraph{

	private final String path;
	private final String altText;
	private final int width, height;

	public ImageParagraph(String path, String altText, int size){
		this(path, altText, size, size);
	}

	public ImageParagraph(String path, String altText, int width, int height){
		this.path = path;
		this.altText = altText;
		this.width = width;
		this.height = height;
	}

	public String content(){
		return "<img src='%s' alt='%s' width='%s' height='%s' />".formatted(path, altText, width, height);
	}
}