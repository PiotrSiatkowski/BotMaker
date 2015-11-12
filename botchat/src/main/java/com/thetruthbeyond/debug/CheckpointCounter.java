package com.thetruthbeyond.debug;

/**
 * Created by Peter Siatkowski on 2015-11-11.
 * Class used to trace amount of checkpoints used in applicatyion.
 */
public class CheckpointCounter {

    private static final int POSITIONS = 3;
    private static int checkpoint = 1;

    public static String nextCheckpoint() {
        String value = Integer.toString(checkpoint);

        StringBuilder message = new StringBuilder(20);
        message.append("Checkpoint ");
        for(int i = 0, n = POSITIONS - value.length(); i != n; i++)
            message.append(" ");
        message.append(value);

        checkpoint++;
        return message.toString();
    }
}
