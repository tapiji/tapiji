package org.eclipse.e4.tapiji.git.core.internal.diff;


import static org.eclipse.jgit.lib.Constants.encodeASCII;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffHunk;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.util.RawParseUtils;


public class TapijiDiffFormatter extends DiffFormatter {

    private int linesLeft = 0;
    private int linesRight = 0;
    private int linesAdded = 0;
    private int linesDeleted = 0;

    private DiffHunk section = null;
    private final OutputStream os;
    private boolean withHooks;

    public TapijiDiffFormatter(OutputStream out, boolean withHooks) {
        super(out);
        this.os = out;
        this.withHooks = withHooks;
    }

    @Override
    protected void writeHunkHeader(int aStartLine, int aEndLine, int bStartLine, int bEndLine) throws IOException {
        os.write('@');
        os.write('@');
        writeRange('-', aStartLine + 1, aEndLine - aStartLine);
        writeRange('+', bStartLine + 1, bEndLine - bStartLine);
        os.write(' ');
        os.write('@');
        os.write("@\n".getBytes());
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
        switch (prefix) {
            case '+':
                linesAdded++;
                os.write(("|+" + (linesRight++) + "| " + text.getString(cur) + "\n").getBytes());
                break;
            case '-':
                linesDeleted++;
                os.write(("-" + (linesLeft++) + "| | " + text.getString(cur) + "\n").getBytes());
                break;
            default:
                os.write((+(linesLeft++) + " |" + (linesRight++) + "| " + text.getString(cur) + "\n").getBytes());
                break;
        }
    }

    public DiffFile get() {
        DiffFile fileDiff = new DiffFile();
        fileDiff.setDeleted(linesDeleted);
        fileDiff.setAdded(linesAdded);
        Stream.of(RawParseUtils.decode(((ByteArrayOutputStream) os).toByteArray()).split("\n"))
            .filter(notStartsWith.apply("index")
                .and(notStartsWith.apply("new file"))
                .and(notStartsWith.apply("\\ No newline"))
                .and(notStartsWith.apply("---"))
                .and(notStartsWith.apply("+++")))
            .forEach(line -> {
                System.out.println(line);
                if (line.startsWith("@@") && line.endsWith("@@")) {
                    if (section != null) {
                        fileDiff.addHunk(section);
                    }
                    section = new DiffHunk();
                    section.setHeader(line);
                } else if (line.startsWith("diff")) {

                } else {
                    if (section != null) {
                        String[] diff = line.split("\\|");
                        if (diff.length >= 3) {
                            section.addLineDiff(new DiffLine(diff[0], diff[1], diff[2]));
                        }
                    }

                }
            });
        fileDiff.addHunk(section);
        return fileDiff;
    }

    private final Function<String, Predicate<String>> notStartsWith = arg -> line -> !line.startsWith(arg);
}
