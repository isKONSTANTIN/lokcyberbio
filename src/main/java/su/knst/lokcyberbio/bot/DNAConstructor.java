package su.knst.lokcyberbio.bot;

import su.knst.lokcyberbio.interpreter.DNACommand;

import java.util.ArrayList;

public class DNAConstructor {
    protected ArrayList<Integer> dna = new ArrayList<>();

    public static DNAConstructor create() {
        return new DNAConstructor();
    }

    protected DNAConstructor() {

    }

    public DNAConstructor addCommand(DNACommand command, int arg) {
        dna.add(command.ordinal());

        if (command.needArgument)
            dna.add(arg);

        return this;
    }

    public DNAConstructor addCommand(DNACommand command) {
        return addCommand(command, -1);
    }

    public DNAConstructor empty() {
        dna.add(-1);

        return this;
    }

    public int[] build() {
        int[] dna = new int[this.dna.size()];

        for (int i = 0; i < dna.length; i++)
            dna[i] = this.dna.get(i);

        return dna;
    }
}
