package odyssey.ru.linglibrary.subtitle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subtitle {
    private Timestamp startTime, endTime;
    private final List<String> lines;

    /* Create a new Subtitle with the given start and end times. */
    public Subtitle(Timestamp startTime, Timestamp endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        lines = new ArrayList<>();
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void clearLines() {
        lines.clear();
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void removeLine(String line) {
        lines.remove(line);
    }

    public void removeLine(int index) {
        lines.remove(index);
    }

    public String getLine(int index) {
        return lines.get(index);
    }

    public List<String> getLines() {
        return lines;
    }

    public int getMiddle() {

        int start = this.getStartTime().getAllMilliseconds();
        int end =   this.getEndTime().getAllMilliseconds();
        return (end - start)/2;
    }

    public int getLenght() {
        int count = 0;
        for (String line : lines) {
            count += line.length();
        }

        return count;
    }

    public int getStrs() {
        return lines.size();
    }


    /* Compiles subtitle into a string with the given subtitle index. */
    public String compile(int index) {
        String subtitle = "";

        subtitle += Integer.toString(index) + "\n";
        subtitle += startTime.compile() + " --> " + endTime.compile() + "\n";

        for (String line : lines) {
            subtitle += line + "\n";
        }

        subtitle += "\n";
        return subtitle;
    }

    public static String formatLine(String line) {
        /* Replace CRLF with LF for neatness. */
        line = line.replace("\r\n", "\n");
		
		/* Empty line marks the end of a subtitle, replace it with a space.  */
        line = line.replace("\n\n", "\n \n");

        return line;
    }

    public String getFullLines() {

        StringBuilder sb =  new StringBuilder();

        for (String e : lines) {
            sb.append(e.replaceAll("<(.)+?>", "").replaceAll("<(\n)+?>", "") + " ");
        }
        return sb.toString();
    }

    public List<String> getFullWords() {


        List<String> list = new ArrayList<>();

        for (String e : lines) {
            String str = stripChars(e);

            String ptr = ":";


            Pattern pattern = Pattern.compile(ptr);
            Matcher matcher = pattern.matcher(str);

            if (matcher.find()) {
                String[] arrWithSplit = str.split(":");
                if (arrWithSplit.length > 1) {
                    str = arrWithSplit[1];
                } else {
                    str = "";
                }
            }

            String[] arr = str.split(" |\n");
            for (int i = 0; i < arr.length; i++) {
                if (!arr[i].isEmpty()) {
                    list.add(arr[i].toLowerCase());
                }
            }
        }

        return list;
    }

    public static String stripChars(String xmlStr) {
        xmlStr = xmlStr.replaceAll("<(.)+?>", "");
        xmlStr = xmlStr.replaceAll("<(\n)+?>", "");
        xmlStr = xmlStr.replaceAll("[(](.)+?[)]", "");
        String ptr = "([\"_.~/?\\#\\[\\]@!$&<>\\^*-+`()*+,;=-])+";
        String result = xmlStr.replaceAll(ptr, "");
        return result;
    }

}
