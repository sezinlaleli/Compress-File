//sezin laleli 191101040

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class myCompress {
    public static ArrayList<Character> myAlphabet;
    public static ArrayList<String> myCount;

    public static void main(String[] args) {
        String mode = args[0];
        String fileName = args[1];

        //compress mode
        if (mode.charAt(1) == 'c') {
            int total = 0;
            myAlphabet = new ArrayList<>(); //array of different characters
            myCount = new ArrayList<>();
            ArrayList<Integer> myDecArray = new ArrayList<>();
            String myBits = "";
            //read the file and find how many different characters
            int newL = 1;
            try {
                FileInputStream fis = new FileInputStream(fileName);
                Scanner sc = new Scanner(fis, "UTF-8");
                while (sc.hasNextLine()) {
                    String s = sc.nextLine();
                    total += createA(s);
                }
                if (newL == 1) {
                    myAlphabet.add('\n');
                    total++;
                    newL = 0;
                }
                sc.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                System.exit(0);
            }

            //find the least bit need to show in binary
            int max = 1;
            while (max < myAlphabet.size())
                max *= 2;
            max = (int) Math.sqrt(max);
            //match characters with number
            for (int i = 0; i < myAlphabet.size(); i++) {
                myCount.add(toBinary(i, max));
            }

            //read file and turn them to binary string
            try {
                FileInputStream fis = new FileInputStream(fileName);
                Scanner sc = new Scanner(fis, "UTF-8");
                while (sc.hasNextLine()) {
                    String s = sc.nextLine();
                    myBits += makeString(s);
                }
                sc.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                System.exit(0);
            }

            //System.out.println(myBits);

            String temp = "";
            //turn strings all 8 characters to decimal
            for (int i = 0; i < myBits.length(); i += 8) {
                int t = 0;
                temp = "";
                while ((t < 8) && ((i + t) < myBits.length())) {
                    temp += myBits.charAt(i + t);
                    t++;
                }
                //put decimals to byte array
                //System.out.println("i: " + toDecimal(Integer.parseInt(temp)));
                myDecArray.add(toDecimal(Integer.parseInt(temp)));
            }

            //write decimals to byte array
            byte byteArray[] = new byte[myDecArray.size()];
            for (int i = 0; i < myDecArray.size(); i++) {
                //System.out.println(myDecArray.get(i));
                byteArray[i] = myDecArray.get(i).byteValue();
                //System.out.println("b: " + myDecArray.get(i).byteValue());
            }

            //write to binary file
            String outputFile = fileName + ".C";
            try {
                FileOutputStream fos = new FileOutputStream(outputFile);
                ObjectOutputStream oo = new ObjectOutputStream(fos);
                //write how many of different characters there are to read
                oo.writeByte((myAlphabet.size()));
                //write whether the last byte contains 4 or 8 its
                int mod = (int) (total * max) % 8;
                oo.writeByte((mod));
                //write all different characters
                for (int i = 0; i < myAlphabet.size(); i++) {
                    //oo.writeUTF("" + myAlphabet.get(i));
                    oo.writeChar(myAlphabet.get(i));
                }
                //write characters codes
                //oo.writeUTF(myBits);
                //write bytes
                for (int i = 0; i < byteArray.length; i++) {
                    oo.writeByte(byteArray[i]);
                }
                oo.close();
            } catch (IOException e) {
                System.out.println("Error while writing file!");
            }
            File myObj = new File(fileName);
            myObj.delete();
            //open compressed file
        } else if (mode.charAt(1) == 'x') {
            String output = fileName.substring(0, fileName.length() - 2);

            PrintWriter pw = null;
            try {
                pw = new PrintWriter(output, "UTF-8");
            } catch (FileNotFoundException e) {
                System.exit(0);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            myAlphabet = new ArrayList<>(); //array of different characters
            ArrayList<Integer> myDecArray = new ArrayList<>();
            ArrayList<Byte> byteArray = new ArrayList<>();
            int mod = 0;
            //read binary file
            try {
                FileInputStream fis = new FileInputStream(fileName);
                ObjectInputStream oi = new ObjectInputStream(fis);
                int go = oi.readByte();
                mod = oi.readByte();
                char t;
                //different characters array
                for (int i = 0; i < go; i++) {
                    t = oi.readChar();
                    myAlphabet.add(t);
                }
                //read characters codes
                //String myBits = oi.readUTF();
                //bytes

                while (true) {
                    byteArray.add(oi.readByte());
                }
                //oi.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            } catch (EOFException e) {

            } catch (IOException e) {
                System.out.println("Error while reading file!");
            }

            //byte do dec
            for (int i = 0; i < byteArray.size(); i++) {
                //System.out.println(byteArray.get(i));
                if (byteArray.get(i) < 0) {
                    myDecArray.add(256 + byteArray.get(i));
                } else {
                    myDecArray.add(0 + byteArray.get(i));
                }
            }

            //find the least bit need to show in binary
            int max = 1;
            while (max < myAlphabet.size())
                max *= 2;
            max = (int) Math.sqrt(max);

            //System.out.println("mod: " + mod);
            //System.out.println("max: " + max);

            ArrayList<Integer> bitArray = new ArrayList<>();
            String t = "";
            for (int i = 0; i < myDecArray.size(); i++) {
                if (i == myDecArray.size() - 1) {
                    if (mod == 0) {
                        t = toBinary(myDecArray.get(i), 8);
                    } else {
                        t = toBinary(myDecArray.get(i), mod);
                    }

                } else {
                    t = toBinary(myDecArray.get(i), 8);
                }
                //System.out.println("d: " + myDecArray.get(i));
                //System.out.println(t);
                for (int j = 0; j < t.length(); j++) {
                    bitArray.add(Integer.parseInt("" + t.charAt(j)));
                }
            }
            for (int i = 0; i < bitArray.size() - max; i += max) {
                int n = 0;
                String code = "";
                while ((n < max) && (n + i) < bitArray.size()) {
                    code += bitArray.get(n + i);
                    n++;
                }
                //System.out.println(code);
                pw.print(myAlphabet.get(toDecimal(Integer.parseInt(code))));
            }
            pw.close();
        }
        File myObj = new File(fileName);
        myObj.delete();
    }

    //find how much different characters
    public static int createA(String s) {
        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            total++;
            if (!myAlphabet.contains(s.charAt(i))) {
                myAlphabet.add(s.charAt(i));
            }
        }
        return total;
    }

    //convert decimal to binary
    public static String toBinary(int decimal, double n) {
        String s = "";
        int binary[] = new int[40];
        int index = 0;
        while (decimal > 0) {
            binary[index++] = decimal % 2;
            decimal = decimal / 2;
        }
        for (int i = index - 1; i >= 0; i--) {
            s += binary[i];
        }

        while (s.length() < n) {
            s = "0" + s;
        }

        return s;
    }

    //convert binary to decimal
    public static int toDecimal(int binary) {
        int decimal = 0;
        int n = 0;
        while (true) {
            if (binary == 0) {
                break;
            } else {
                int temp = binary % 10;
                decimal += temp * Math.pow(2, n);
                binary = binary / 10;
                n++;
            }
        }
        return decimal;
    }

    //create string by bits
    public static String makeString(String s) {
        String myBits = "";
        for (int i = 0; i < s.length(); i++) {
            myBits += myCount.get(myAlphabet.indexOf(s.charAt(i)));
        }
        myBits += myCount.get(myAlphabet.indexOf('\n'));
        return myBits;
    }
}