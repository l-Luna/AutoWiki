package auto_wiki.layout;

public class LinkParagraph implements Paragraph{

	private final String text, target, tooltip;

	public LinkParagraph(String text, String target){
		this(text, target, text);
	}

	public LinkParagraph(String text, String target, String tooltip){
		this.text = text;
		this.target = target;
		this.tooltip = tooltip;
	}

	public String content(){
		return "[%s](%s \"%s\")".formatted(text, target, tooltip);
	}
}