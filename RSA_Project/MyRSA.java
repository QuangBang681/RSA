import java.math.BigInteger;
import java.util.Random;

public class MyRSA {

    // Function to calculate the Greatest Common Divisor (GCD) of two BigIntegers.
    static BigInteger calculateGCD(BigInteger a, BigInteger b) {
        while (true) {
            if (a.compareTo(BigInteger.ZERO) == 0) return b;
            if (b.compareTo(BigInteger.ZERO) == 0) return a;
            if (a.compareTo(b) == 1) {
                a = a.mod(b);
            } else {
                b = b.mod(a);
            }
        }
    }

    // Function to find the modular multiplicative inverse of a modulo b.
    static BigInteger[] calculateModularInverse(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ONE) == 0) {
            return new BigInteger[]{
                    BigInteger.ONE,
                    a.subtract(BigInteger.ONE)
            };
        }
        if (a.compareTo(BigInteger.ONE) == 0) {
            return new BigInteger[]{
                    BigInteger.ONE,
                    BigInteger.ZERO
            };
        }
        if (a.compareTo(b) != -1) {
            BigInteger[] vals = calculateModularInverse(a.mod(b), b);
            return new BigInteger[]{
                    vals[0],
                    a.divide(b).multiply(vals[0]).add(vals[1])
            };
        }
        BigInteger[] vals = calculateModularInverse(a, b.mod(a));
        return new BigInteger[]{
                b.divide(a).multiply(vals[1]).add(vals[0]),
                vals[1]
        };
    }

    // Function to calculate a^b mod n using recursive exponentiation.
    static BigInteger modularExponentiation(BigInteger a, BigInteger b, BigInteger n) {
        BigInteger two = new BigInteger("2");
        if (b.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.ONE;
        }
        BigInteger res = modularExponentiation(a, b.divide(two), n);
        if (b.mod(two).compareTo(BigInteger.ZERO) == 0) {
            return res.multiply(res).mod(n);
        }
        return res.multiply(res).mod(n).multiply(a).mod(n);
    }

    // Function to calculate the modular multiplicative inverse of e modulo (p-1)*(q-1).
    static BigInteger calculatePhiInverse(BigInteger e, BigInteger p, BigInteger q) {
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        BigInteger qMinus1 = q.subtract(BigInteger.ONE);
        BigInteger phi = pMinus1.multiply(qMinus1);
        BigInteger[] arr = calculateModularInverse(e, phi);
        BigInteger d = arr[0];
        return d;
    }

    // Function to generate a random BigInteger in the range [m, n).
    static BigInteger generateRandomBigInteger(BigInteger m, BigInteger n) {
        Random randNum = new Random();
        int len = n.bitLength();
        BigInteger res = new BigInteger(len, randNum);
        while ((res.compareTo(n) != -1) || (res.compareTo(m) == -1)) {
            randNum = new Random();
            res = new BigInteger(len, randNum);
        }
        return res;
    }

    // Function to perform the Miller-Rabin primality test.
    static boolean millerRabinTest(BigInteger n, BigInteger a) {
        BigInteger nMinus1 = n.subtract(BigInteger.ONE);
        BigInteger two = new BigInteger("2");
        BigInteger copy = n.subtract(BigInteger.ONE);
        int s = 0;
        while (copy.mod(two).compareTo(BigInteger.ZERO) == 0) {
            s++;
            copy = copy.divide(two);
        }
        BigInteger y = BigInteger.ONE;
        BigInteger d = nMinus1.divide(two.pow(s));
        BigInteger x = modularExponentiation(a, d, n);
        for (int i = 0; i < s; i++) {
            y = modularExponentiation(x, two, n);
            if ((y.compareTo(BigInteger.ONE) == 0) && (x.compareTo(BigInteger.ONE) != 0) && (x.compareTo(nMinus1) != 0)) {
                return false;
            }
            x = y;
        }
        if (y.compareTo(BigInteger.ONE) != 0) {
            return false;
        }
        return true;
    }

    // Function to check if a number is prime using the Miller-Rabin test and additional checks.
    static boolean isProbablePrime(BigInteger n) {
        boolean bool = true;
        int t = 15;
        BigInteger a = new BigInteger("1");
        BigInteger one = a;

        for (int i = 0; i < t; i++) {
            a = generateRandomBigInteger(one, n);
            if (calculateGCD(a, n).compareTo(BigInteger.ONE) != 0) {
                return false;
            }
            bool = millerRabinTest(n, a);
            if (!bool) {
                return bool;
            }
        }

        String[] primes = {"2", "3", "5", "7", "11", "13", "17", "19", "23", "29", "31", "37", "41", "43", "47", "53",
                "59", "61", "67", "71", "73", "79", "83", "91", "101", "103", "107", "109", "113", "127", "131", "137", "139", "149", "151", "157", "163"};
        for (int i = 0; i < 37; i++) {
            a = new BigInteger(primes[i]);
            if (n.mod(a).compareTo(BigInteger.ZERO) == 0) {
                return false;
            }
        }
        return bool;
    }

    // Function to generate a random prime number with a given number of bits.
    static BigInteger generateRandomPrime(int bit) {
        Random randNum = new Random();
        BigInteger res = new BigInteger(bit, randNum);
        BigInteger two = new BigInteger("2");
        BigInteger four = new BigInteger("4");
        BigInteger six = new BigInteger("6");

        boolean isPrime = isProbablePrime(res);
        if (res.mod(two).compareTo(BigInteger.ZERO) == 0) {
            res = res.add(BigInteger.ONE);
        }
        while (!isPrime) {
            if (res.mod(six).compareTo(BigInteger.ONE) == 0) {
                res = res.add(four);
            } else {
                res = res.add(two);
            }
            isPrime = isProbablePrime(res);
        }
        return res;
    }

    // Function to generate a strong prime number with a given number of bits and a specified e.
    static BigInteger[] generateStrongPrime(int bit, BigInteger e) {
        BigInteger two = new BigInteger("2");

        BigInteger ten = new BigInteger("10");
        BigInteger lowerBound = ten.pow(154);
        BigInteger upperBound = two.pow(512).subtract(BigInteger.ONE);
        BigInteger res = generateRandomBigInteger(lowerBound, upperBound);
        boolean result = false;
        BigInteger p0 = generateRandomPrime(128);
        while (e.mod(p0).compareTo(BigInteger.ZERO) == 0) {
            p0 = generateRandomPrime(128);
        }
        BigInteger p1 = generateRandomPrime(128);
        while ((p1.compareTo(p0) == 0) || (e.mod(p1).compareTo(BigInteger.ZERO) == 0)) {
            p1 = generateRandomPrime(128);
        }
        p1 = p1.multiply(two);

        BigInteger increment = p0.multiply(p1);

        BigInteger[] inv1 = calculateModularInverse(p1, p0);
        BigInteger crt1 = inv1[0];
        crt1 = crt1.multiply(p1);

        BigInteger[] inv2 = calculateModularInverse(p0, p1);
        BigInteger crt2 = p1.subtract(inv2[0]);
        crt2 = crt2.multiply(p0);

        BigInteger crt = crt1.add(crt2).add(increment);
        BigInteger resmod = res.mod(increment);
        res = res.add(crt.subtract(resmod).mod(increment));
        increment = increment.multiply(two);

        while (true) {
            boolean possiblePrime = true;
            if (possiblePrime) {
                if (calculateGCD(e, res.subtract(BigInteger.ONE)).compareTo(BigInteger.ONE) != 0) {
                    possiblePrime = false;
                }
            }
            if (possiblePrime) {
                result = isProbablePrime(res);
                if (result) {
                    break;
                }
            }
            res = res.add(increment);
        }
        return new BigInteger[]{res, p0, p1.divide(two)};
    }

    // Function to encrypt a message m using public key (e, p, q).
    static BigInteger performEncryption(BigInteger m, BigInteger e, BigInteger p, BigInteger q) {
        BigInteger N = p.multiply(q);
        return modularExponentiation(m, e, N);
    }

    // Function to decrypt a ciphertext c using private key (d, p, q).
    static BigInteger performDecryption(BigInteger c, BigInteger d, BigInteger p, BigInteger q) {
        BigInteger N = p.multiply(q);
        return modularExponentiation(c, d, N);
    }

    // Main function demonstrating the RSA key generation, encryption, and decryption process.
    public static void main(String args[]) throws Exception {
        BigInteger two = new BigInteger("2");

        /*  The first step is to generate public and private keys. The public key is created by generating a random prime number, e, with a length of 1024 bits.
            If e is even, it is incremented by one to ensure it is odd.
        */
        System.out.println("Generate e");
        Random rand = new Random();
        BigInteger e = new BigInteger(1024, rand);
        if (e.mod(two).compareTo(BigInteger.ZERO) == 0) {
            e = e.add(BigInteger.ONE);
        }
        System.out.println("\t" + e);
        System.out.println("\n");

        System.out.println("Generate Strong-prime p:");
        BigInteger p = new BigInteger("512");
        BigInteger[] list = generateStrongPrime(512, e);
        p = list[0];
        BigInteger p0 = list[1];
        BigInteger p1 = list[2];
        System.out.println("\t" + p);
        System.out.println("p - 1 is divisible by a Large-prime: " + p0);
        System.out.println("p + 1 is divisible by a Large-prime: " + p1);
        System.out.println("\n");

        /*  Generate two strong prime numbers, p and q. These primes have a length of 512 bits and are generated by randomly selecting a large prime number and checking its strength
            by verifying the divisibility of (p-1) and (p+1) by another large prime, similarly for q-1 and q+1.
        */
        System.out.println("Generate Strong-prime q");
        BigInteger q = new BigInteger("512");
        BigInteger[] list2 = generateStrongPrime(512, e);
        q = list2[0];
        BigInteger q0 = list2[1];
        BigInteger q1 = list2[2];
        System.out.println("\t" + q);
        System.out.println("q - 1 is divisible by a Large-prime: " + q0);
        System.out.println("q + 1 is divisible by a Large-prime: " + q1);
        System.out.println("\n");

        System.out.println("Message:"); // Generate the message the sender wants to send with the condition that its length is less than the product of p and q.
        Random randNum = new Random();
        BigInteger message = new BigInteger(1024, randNum);
        // Ensure that the message is less than p*q
        message = message.mod(p.multiply(q));
        System.out.println(message);
        System.out.println("\n");

        /*  Encrypt the message using the public key (e, p, q).
        */
        System.out.println("Encryption:");
        BigInteger ciphertext = performEncryption(message, e, p, q);
        System.out.println(ciphertext);
        System.out.println("\n");

        /*  Calculate the private key d by computing the modular inverse of e modulo (p-1)*(q-1) using the PhiInv function.
        */
        System.out.println("Calculate Inverse modulo phi:");
        BigInteger privateKey = calculatePhiInverse(e, p, q);
        System.out.println(privateKey);
        System.out.println("\n");

        /*  Decrypt the ciphertext using the private key d and the primes p and q.
        */
        System.out.println("Decryption:");
        message = performDecryption(ciphertext, privateKey, p, q);
        assert (message.compareTo(p.multiply(q)) == -1);
        System.out.println(message);
        System.out.println("\n");
    }
}
