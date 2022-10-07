package auto_wiki.layout;

import java.util.List;
import java.util.stream.Collectors;

public class SeqParagraph implements Paragraph{

	private final List<Paragraph> components;

	public SeqParagraph(List<Paragraph> components){
		this.components = components;
	}

	public SeqParagraph(Paragraph... components){
		this(List.of(components));
	}

	public String content(){
		return components.stream().map(Paragraph::content).collect(Collectors.joining());
	}
}