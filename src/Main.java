import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(2);

        List<String> words = new ArrayList<>();
        for(int i = 0; i <= 1000; i++) {
            if(i % 2 == 0) {
                words.add("String with A " + i);
            } else {
                words.add("String " + i);
            }
        }

        List<String> wordsContainsA = forkJoinPool.invoke(new FilterContainsA(words));

        System.out.println("Words containing A: " + wordsContainsA);
        forkJoinPool.close();
    }
}

class FilterContainsA extends RecursiveTask<List<String>> {
    List<String> list;

    public FilterContainsA(List<String> wordList) {
        this.list = wordList;
    }

    @Override
    protected List<String> compute() {
        if(this.list.size() <= 250) {
            return this.list.stream().filter(word -> word.contains("A")).toList();
        }

        int mid = this.list.size() / 2;
        FilterContainsA filterLeft = new FilterContainsA(this.list.subList(0, mid));
        FilterContainsA filterRight = new FilterContainsA(this.list.subList(mid, this.list.size()));

        filterLeft.fork();
        filterRight.fork();

        List<String> filteredLeft = filterLeft.join();
        List<String> filteredRight = filterRight.join();

        return Stream.concat(filteredLeft.stream(), filteredRight.stream()).toList();
    }
}