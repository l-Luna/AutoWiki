package auto_wiki.layout;

import java.util.ArrayList;
import java.util.List;

public class WikiPage{

	private final List<Element> content = new ArrayList<>();

	public WikiPage append(Element element){
		content.add(element);
		return this;
	}

	public String toMarkdown(){
		StringBuilder builder = new StringBuilder();
		for(Element element : content)
			builder.append(element.content()).append("\n\n");
		return builder.toString();
	}
}