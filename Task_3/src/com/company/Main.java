package com.company;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        play(args);
    }

    public static void play(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {

        HashSet<String> hashSet = new HashSet<>(Arrays.asList(args));

        if (args.length < 3 || args.length % 2 == 0 || hashSet.size() != args.length) {
            System.out.println("Argument entry error!");
            System.exit(0);
        }

        Random random = new Random();
        Scanner in = new Scanner(System.in);
        final int indexComputerMove = random.nextInt(args.length);
        final byte[] key = randomKey();
        final byte[] hmac = getHMAC(key, args[indexComputerMove]);
        System.out.println("HMAC: " + getBytes(hmac));

        int myMove = 0;
        int computerMove;

        System.out.println("Available moves: ");
        for (int i = 0; i < args.length; i++) {
            System.out.println(i + 1 + " - " + args[i]);
        }
        System.out.println("0 - exit");
        System.out.print("Enter your move: ");

        try {
            myMove = in.nextInt();
            if (myMove > args.length || myMove < 0) {
                System.out.println("Input Error!");
                play(args);
                return;
            }
            if (myMove == 0) {
                System.out.println("exit!");
                System.exit(0);
            }
        } catch (Exception e) {
            play(args);
            return;
        }
        computerMove = random.nextInt(args.length);
        System.out.println("My move: " + args[myMove - 1]);
        System.out.println("Computer move: " + args[computerMove]);

        Result result = game(myMove - 1, computerMove, args);

        switch (result) {
            case Win -> System.out.println("You win");
            case Lost -> System.out.println("You lost");
            case Draw -> System.out.println("Draw");
        }
        System.out.println("Key: " + getBytes(key));
    }

    public static Result game(int myMove, int computerMove, String[] args) {
        if (myMove == computerMove) {
            return Result.Draw;
        }

        List<String> win = new ArrayList<>();
        int right = myMove + 1;
        int beginning = 0;

        for (int i = 0; i < args.length / 2; i++) {
            if (right <= args.length - 1) {
                win.add(args[right]);
                right++;
            } else {
                win.add(args[beginning]);
                beginning++;
            }
        }
        boolean isWin = false;
        for (String s : win) {
            if (args[computerMove].equals(s)) {
                isWin = true;
            }
        }
        if (isWin) {
            return Result.Win;
        } else {
            return Result.Lost;
        }
    }

    private static byte[] randomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static String getBytes(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            stringBuilder.append(String.format("%02x", b));
        return stringBuilder.toString();
    }

    private static byte[] getHMAC(byte[] key, String computerMove) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        final String HMAC_ALGO = "HmacSHA512";
        Mac signer = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_ALGO);
        signer.init(keySpec);
        byte[] digest = signer.doFinal(computerMove.getBytes("utf-8"));
        return digest;
    }
}


