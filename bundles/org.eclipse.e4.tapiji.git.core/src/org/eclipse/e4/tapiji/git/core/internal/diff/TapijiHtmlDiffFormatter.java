package org.eclipse.e4.tapiji.git.core.internal.diff;


import static org.eclipse.jgit.lib.Constants.encodeASCII;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.util.RawParseUtils;


public class TapijiHtmlDiffFormatter extends DiffFormatter {

    private OutputStream os;

    public TapijiHtmlDiffFormatter(OutputStream out) {
        super(out);
        this.os = out;
    }

    private int linesLeft, linesRight, linesAdded, linesDeleted;

    private String css = "<style>td {padding:0; margin:0;}</style>";

    @Override
    protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException {
        os.write("<tr><td colspan=\"3\">".getBytes());
        os.write('@');
        os.write('@');
        writeRange('-', aStartLine + 1, aEndLine - aStartLine);
        writeRange('+', bStartLine + 1, bEndLine - bStartLine);
        os.write(' ');
        os.write('@');
        os.write('@');
        os.write("</td></tr>\n".getBytes());
        linesLeft = aStartLine + 1;
        linesRight = bStartLine + 1;
    }

    private void writeRange(final char prefix, final int begin, final int cnt) throws IOException {
        os.write(' ');
        os.write(prefix);
        switch (cnt) {
            case 0:
                // If the range is empty, its beginning number must be the
                // line just before the range, or 0 if the range is at the
                // start of the file stream. Here, begin is always 1 based,
                // so an empty file would produce "0,0".
                //
                os.write(encodeASCII(begin - 1));
                os.write(',');
                os.write('0');
                break;

            case 1:
                // If the range is exactly one line, produce only the number.
                //
                os.write(encodeASCII(begin));
                break;

            default:
                os.write(encodeASCII(begin));
                os.write(',');
                os.write(encodeASCII(cnt));
                break;
        }
    }

    @Override
    protected void writeLine(final char prefix, final RawText text, final int cur) throws IOException {

        os.write("<tr>".getBytes());
        switch (prefix) {
            case '+':
                linesAdded++;
                os.write(("<th></th><th>+" + (linesRight++) + "</th>").getBytes());
                os.write(("<td bgColor=\"#32CD32\">" + text.getString(cur) + "</td>").getBytes());
                break;
            case '-':
                linesDeleted++;
                os.write(("<th>-" + (linesLeft++) + "</th><th></th>").getBytes());
                os.write(("<td bgColor=\"#DC143C\">" + text.getString(cur) + "</td>").getBytes());
                break;
            default:
                os.write(("<th>" + (linesLeft++) + "</th><th>" + (linesRight++) + "</th>").getBytes());
                os.write(("<td>" + text.getString(cur) + "</td>").getBytes());
                break;
        }
        os.write("</tr>".getBytes());

    }

    boolean startDiff = false;

    public String toHtml() {
        startDiff = false;
        StringBuilder sb = new StringBuilder();
        sb.append(css);
        Stream.of(RawParseUtils.decode(((ByteArrayOutputStream) os).toByteArray()).split("\n"))
            .filter(line -> !line.startsWith("index") && !line.startsWith("new file") && !line.startsWith("\\ No newline") && !line.startsWith("---") && !line.startsWith("+++"))
            .forEach(line -> {
                if (line.startsWith("diff")) {

                    if (startDiff) {
                        sb.append("</table>");
                        startDiff = false;
                    } else {

                        sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
                        startDiff = true;
                    }
                } else {

                    sb.append(line);

                }
            });
        return sb.toString();
    }

}
