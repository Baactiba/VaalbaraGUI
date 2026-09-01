import java.util.*;

public class Algorithm
{

    public static void main (String[] args) {
//        solve(8, 350, 512, 0.75, 16, 17);
        solve(5, 60, 512, 0.05, 4,8,9);
    }

    static int size;
    static int storageSize;
    static int hashSetSize;
    static boolean setup = false;
    static int hashSetPower = 0;
    static int hashSetModMap;
    static void solve(int setSize, int maxSum, int cache, double fun_constant, int... set) {
        if (!setup) {
            setup = true;
            storageSize = 1 + maxSum / 32;
            cache *= 1024;
            cache /= 16;
            cache = (int) (cache * fun_constant);
            cache /= (setSize - set.length);
            bptlt(cache);
            size = storageSize + hashSetSize;
        }
        long[] data = new long[size * (setSize - set.length)];
        long[] start = setup(maxSum, set);
        System.arraycopy(start, 0, data, 0, start.length);

        int[] indices = new int[setSize - set.length];
        int[] cumSum = new int[setSize - set.length];
        for (int i : set)
            cumSum[0] += i;
        int index = 0;

        System.out.println(cumSum[0]);


        while (index > -1) {
            int nRem = indices.length - index;
            tAdd: for (int adding = index == 0 ? set[set.length - 1] + 1 : indices[index - 1]; adding <= (maxSum - cumSum[index]) / nRem - (nRem / 2); adding++) {
                int ss = index * size;
                int ss2 = index * size + size;
                System.arraycopy(data, ss, data, ss2, size);



                set(data, ss2, adding, 0b11);
                for (int i = 2; i <= maxSum; i++) {
                    switch (get(data, ss, i)) {
                        case 0b01: {
                            if (!set(data, ss2, i + adding, 0b01)) {
                                continue tAdd;
                            }
                            break;
                        }
                        case 0b10: {
                            long product = (long) i * adding;
                            if (product > maxSum)
                                if (!add(data, ss2 + storageSize, product)) {
                                    continue tAdd;
                                }
                            else
                                if (!set(data, ss2, (int) product, 0b10)) {
                                    continue tAdd;
                                }
                            break;
                        }
                        case 0b11: {
                            if (!set(data, ss2, i + adding, 0b01)) {
                                continue tAdd;
                            }
                            long product = (long) i * adding;
                            if (product > maxSum)
                                if (!add(data, ss2 + storageSize, product)) {
                                    continue tAdd;
                                }
                            else
                                if (!set(data, ss2, (int) product, 0b10)) {
                                    continue tAdd;
                                }
                            break;
                        }
                    }
                }
                long threshold = Long.MAX_VALUE / adding;
                for (int i = ss + storageSize; i < ss + storageSize + hashSetSize; i++) {
                    if (data[i] > 0 && data[i] <= threshold) {
                        long product = data[i] * adding;
                        if (!add(data, ss2 + storageSize, product)) {
                            continue tAdd;
                        }
                    }
                }

                








            }
        }

    }




    static long[] setup(int maxSum, int... set) {
        long[] ret = new long[size];
        set(ret, 0, set[0], 0b11);
        set(ret, 0, set[1], 0b11);
        set(ret, 0, set[0] + set[1], 0b01);
        int prod = set[0] * set[1];
        if (prod > maxSum)
            add(ret, storageSize, prod);
        else
            set(ret, 0, prod, 0b10);
        for (int x = 2; x < set.length; x++) {
            int adding = set[x];
            long[] ret2 = new long[ret.length];
            System.arraycopy(ret, 0, ret2, 0, hashSetSize);
            set(ret2, 0, adding, 0b11);
            for (int i = 2; i <= maxSum; i++) {
                switch (get(ret, 0, i)) {
                    case 0b01:
                        set(ret2, 0, i + adding, 0b01);
                        break;
                    case 0b10: {
                        long product = (long) i * adding;
                        if (product > maxSum)
                            add(ret2, storageSize, product);
                        else
                            set(ret2, 0, (int) product, 0b10);
                        break;
                    }
                    case 0b11: {
                        set(ret2, 0, i + adding, 0b01);
                        long product = (long) i * adding;
                        if (product > maxSum)
                            add(ret2, storageSize, product);
                        else
                            set(ret2, 0, (int) product, 0b10);
                        break;
                    }
                }
            }
            for (int i = storageSize; i < ret.length; i++) {
                if (ret[i] > 0) {
                    long product = ret[i] * adding;
                    add(ret2, storageSize, product);
                }
            }
            ret = ret2;
        }
        return ret;
    }
    static boolean add(long[] set, int offset, long l) {
        long h = l;
        h ^= h >> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= h >> 33;
        int i = ((int) h) & hashSetModMap;
        int step = ((int) (h >> 32) | 1) & 4095;

        while(set[offset + i] != 0) {
            if (set[offset + i] == l)
                return false;
            i = (i + step) & hashSetModMap;
        }
        set[offset + i] = l;
        return true;
    }
    static int get(long[] arr, int offset, int position) {
        return (int) (arr[offset + (position >> 5)] >> (2 * (position & 0b11111))) & 0b11;
    }
    static boolean set(long[] arr, int offset, int position, int value) {
        if (((int) (arr[offset + (position >> 5)] >> (2 * (position & 0b11111))) & 0b11) != 0)
            return false;
        int i = offset + (position >> 5);
        int shift = (position & 31) << 1;
        long mask = 0b11L << shift;
        arr[offset + i] = (arr[offset + i] & ~mask) | ((long) value << shift);
        return true;
    }
    public static void bptlt(int n) {
        int i = 1;
        while (true) {
            hashSetPower = i;
            hashSetSize = 1 << (i++ - 1);
            if (hashSetSize > n) {
                hashSetSize /= 2;
                hashSetPower--;
                hashSetModMap = hashSetSize - 1;
                return;
            }
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