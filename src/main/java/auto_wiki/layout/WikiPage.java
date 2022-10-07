package auto_wiki.layout;

import java.util.ArrayList;
import java.util.List;

public class WikiPage{

	private final List<Paragraph> content = new ArrayList<>();

	public WikiPage append(Paragraph paragraph){
		content.add(paragraph);
		return this;
	}

	public String toMarkdown(){
		StringBuilder builder = new StringBuilder();
		for(Paragraph paragraph : content)
			builder.append(paragraph.content()).append("\n\n");
		return builder.toString();
	}
}