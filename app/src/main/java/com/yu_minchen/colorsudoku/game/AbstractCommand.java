package com.yu_minchen.colorsudoku.game;

import java.util.StringTokenizer;

public abstract class AbstractCommand {

    private interface CommandCreatorFunction {
        AbstractCommand create();
    }

    private static class CommandDef {
        String mLongName;
        String mShortName;
        CommandCreatorFunction mCreator;

        public CommandDef(String longName, String shortName, CommandCreatorFunction creator){
            mLongName = longName;
            mShortName = shortName;
            mCreator = creator;
        }

        public AbstractCommand create() {
            return mCreator.create();
        }

        public String getLongName() {
            return mLongName;
        }

        public String getShortName() {
            return mShortName;
        }
    }

    private static final CommandDef[] commands = {
            new CommandDef(SetCellValueCommand.class.getSimpleName(),"c4",
                    new CommandCreatorFunction() { public AbstractCommand create() {return new SetCellValueCommand();} }),
    };

    public static AbstractCommand deserialize(StringTokenizer data) {
        String cmdShortName = data.nextToken();
        for (CommandDef cmdDef: commands) {
            if (cmdDef.getShortName().equals(cmdShortName)) {
                AbstractCommand cmd = cmdDef.create();
                cmd._deserialize(data);
                return cmd;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown command class '%s'.", cmdShortName));
    }

    protected void _deserialize(StringTokenizer data) {

    }

    public void serialize(StringBuilder data) {
        String cmdLongName = getCommandClass();
        for (CommandDef cmdDef: commands) {
            if (cmdDef.getLongName().equals(cmdLongName)) {
                data.append(cmdDef.getShortName()).append("|");
                return;
            }
        }

        throw new IllegalArgumentException(String.format("Unknown command class '%s'.", cmdLongName));
    }

    public String getCommandClass() {
        return getClass().getSimpleName();
    }

    abstract void execute();

}

