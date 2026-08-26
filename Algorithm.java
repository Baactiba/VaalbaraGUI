import java.util.*;

public class Algorithm
{
    static int size;
    static int storageSize;
    static int hashSetSize;
    static boolean setup = false;
    static void solve(int max, int cache, double fun_constant, int... set) {
        if (!setup) {
            setup = true;
            storageSize = (max + 1) / 32;
            hashSetSize = 3;
            cache *= 1024;
            cache /= 16;
            cache = (int) (cache * fun_constant);
            cache /= (max - set.length);
            cache = findByBruteForce(cache);
        }
    }








    public static int findByBruteForce(int n) {                 // thx Baeldung for this and the method under
        for (int i = n - 1; i >= 2; i--) {
            if (isPrime(i)) {
                return i;
            }
        }
        return -1; // Return -1 if no prime number is found
    }
    public static boolean isPrime(int number) {
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}