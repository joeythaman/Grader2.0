import java.math.BigDecimal;
import java.util.*;

public class Runner {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Scantron scan = new Scantron();
        ArrayList<ArrayList<Character>> answers = Scantron.scan(args[0]);
        ArrayList<ArrayList<Character>> newAnswers = new ArrayList<ArrayList<Character>>();;
        for (int i = answers.size()-1; i >= 0; i--) {
            if (answers.get(i).size() > 0) {
                newAnswers.add(answers.get(i));
            } else {
                break;
            }
        }
        System.out.println(newAnswers);
    }

}
