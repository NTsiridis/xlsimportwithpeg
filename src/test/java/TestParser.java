import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/*
Test the parsing functionality
 */
public class TestParser {

    @Test
    public void testParsing() throws IOException {
        InputStream in = new FileInputStream("./src/test/resources/GRPs.xlsx");
        char[] input = XLSUtils.stringify(in, 0,
                String.valueOf(ParboiledGRPParser.COLUMN_SEPARATOR),
                String.valueOf(ParboiledGRPParser.ROW_SEPARATOR));

//        GRPData data = (GRPData) result.valueStack.pop();
//        assertNotNull(data);

        ParboiledGRPParser parser = Parboiled.createParser(ParboiledGRPParser.class);
        ReportingParseRunner runner = new ReportingParseRunner(parser.GRPData());
        ParsingResult<?> result = runner.run(input);

        if (!result.parseErrors.isEmpty()) {
            result.parseErrors.forEach( err -> System.out.println(ErrorUtils.printParseError(err)));
        }
        else {
            System.out.println(printNodeTree(result) + '\n');
        }

//        GRPData data = (GRPData) result.valueStack.pop();
//        assertNotNull(data);


    }


}
