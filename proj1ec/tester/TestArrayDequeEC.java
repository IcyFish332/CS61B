package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> broken = new StudentArrayDeque<>();
        StringBuilder msg = new StringBuilder();

        int N = 5000;
        int size = 0;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                msg.append("addLast(" + randVal + ")\n");
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                broken.addFirst(randVal);
                msg.append("addFirst(" + randVal + ")\n");
            } else if (operationNumber > 1 && correct.size() <= 0) {
                msg.append("isEmpty()\n");
                assertTrue(msg.toString(), broken.isEmpty());
                continue;
            } else if (operationNumber == 2) {
                int firstRemoved = correct.removeFirst();
                int firstRemoved2 = broken.removeFirst();
                msg.append("removeFirst()\n");
                assertEquals(msg.toString(), firstRemoved, firstRemoved2);
            } else if (operationNumber == 3) {
                int lastRemoved = correct.removeLast();
                int lastRemoved2 = broken.removeLast();
                msg.append("removeLast()\n");
                assertEquals(msg.toString(), lastRemoved, lastRemoved2);
            }
        }
    }

}
