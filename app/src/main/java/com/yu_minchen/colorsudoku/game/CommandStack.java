package com.yu_minchen.colorsudoku.game;

import java.util.Stack;
import java.util.StringTokenizer;

public class CommandStack {
    private Stack<AbstractCommand> mCommandStack = new Stack<AbstractCommand>();

    //	commands. CellCollection should be able to validate itself on change.
    private CellCollection mCells;

    public CommandStack(CellCollection cells) {
        mCells = cells;
    }

    public static CommandStack deserialize(String data, CellCollection cells) {
        StringTokenizer st = new StringTokenizer(data, "|");
        return deserialize(st, cells);
    }

    public static CommandStack deserialize(StringTokenizer data, CellCollection cells) {
        CommandStack result = new CommandStack(cells);
        int stackSize = Integer.parseInt(data.nextToken());
        for (int i = 0; i < stackSize; i++) {
            AbstractCommand command = AbstractCommand.deserialize(data);
            result.push(command);
        }

        return result;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        serialize(sb);
        return sb.toString();
    }

    public void serialize(StringBuilder data) {
        data.append(mCommandStack.size()).append("|");
        for (int i = 0; i < mCommandStack.size(); i++) {
            AbstractCommand command = mCommandStack.get(i);
            command.serialize(data);
        }
    }

    public boolean empty() {
        return mCommandStack.empty();
    }

    public void execute(AbstractCommand command) {
        push(command);
        command.execute();
    }

    private void push(AbstractCommand command) {
        if (command instanceof AbstractCellCommand) {
            ((AbstractCellCommand) command).setCells(mCells);
        }
        mCommandStack.push(command);
    }

    private AbstractCommand pop() {
        return mCommandStack.pop();
    }

    private void validateCells() {
        mCells.validate();
    }
}
