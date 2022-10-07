package auto_wiki.layout;

import java.util.List;

public class TableParagraph implements Paragraph{

	private final List<List<String>> columns;

	public TableParagraph(List<List<String>> columns){
		if(columns.size() > 0){
			int rows = columns.get(0).size();
			if(columns.stream().anyMatch(x -> x.size() != rows))
				throw new IllegalArgumentException("All columns of a TableElement must have the same number of rows");
			if(rows <= 1)
				throw new IllegalArgumentException("A TableElement must have at least 2 rows");
			for(List<String> column : columns)
				for(String s : column)
					if(s.contains("\n"))
						throw new IllegalArgumentException("A TableElement cell cannot have a newline; use <br>");
		}
		this.columns = columns;
	}

	public String content(){
		if(columns.size() == 0)
			return "";
		StringBuilder content = new StringBuilder();
		int rows = columns.get(0).size();
		for(int row = 0; row < rows; row++){
			content.append("|");
			for(List<String> column : columns)
				content.append(" ").append(column.get(row)).append(" |");
			content.append("\n");
			// dashes
			if(row == 0)
				content.append("|").append("---|".repeat(columns.size())).append("\n");
		}
		return content.toString();
	}
}