package DataStructures.Hash;

import DataStructures.MyInteger;

// QuadraticProbingHashTable abstract class
//
// CONSTRUCTION: with an approximate initial size or a default.
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x
// Hashable find( x )     --> Return item that matches x
// void makeEmpty( )      --> Remove all items
// int hash( String str, int tableSize )
//                        --> Static method to hash strings

/**平方探测法
 * Probing table implementation of hash tables. Note that all "matching" is
 * based on the equals method.
 * 
 */
public class QuadraticProbingHashTable {
	/**
	 * 构造器
	 */
	public QuadraticProbingHashTable() {
		this(DEFAULT_TABLE_SIZE);
	}

	/**
	 * 构造器
	 * 
	 * @param size
	 *            the approximate initial size.
	 */
	public QuadraticProbingHashTable(int size) {
		allocateArray(size);
		makeEmpty();
	}

	/**
	 * 插入
	 * 
	 * @param x
	 *            the item to insert.
	 */
	public void insert(Hashable x) {
		// Insert x as active
		int currentPos = findPos(x);
		if (isActive(currentPos))
			return;

		array[currentPos] = new HashEntry(x, true);

		// 容量到一半，展开哈希表
		if (++currentSize > array.length / 2)
			rehash();
	}

	/**
	 * 展开哈希表
	 */
	private void rehash() {
		HashEntry[] oldArray = array;

		// 新建一个二倍大小的新表
		allocateArray(nextPrime(2 * oldArray.length));
		currentSize = 0;

		// 复制表中元素
		for (int i = 0; i < oldArray.length; i++)
			if (oldArray[i] != null && oldArray[i].isActive)
				insert(oldArray[i].element);

		return;
	}

	/**
	 * 使用平方探测进行查找
	 * 
	 * @param x
	 *            要查找的对象.
	 * @return the position where the search terminates.
	 */
	private int findPos(Hashable x) {
		int collisionNum = 0;
		int currentPos = x.hash(array.length);

		while (array[currentPos] != null
				&& !array[currentPos].element.equals(x)) {
			currentPos += 2 * ++collisionNum - 1; // Compute ith probe
			if (currentPos >= array.length) // Implement the mod
				currentPos -= array.length;
		}

		return currentPos;
	}

	/**
	 * 删除
	 * 
	 * @param x
	 *            the item to remove.
	 */
	public void remove(Hashable x) {
		int currentPos = findPos(x);
		if (isActive(currentPos))
			array[currentPos].isActive = false;
	}

	/**
	 * 查找
	 * 
	 * @param x
	 *            the item to search for.
	 * @return the matching item.
	 */
	public Hashable find(Hashable x) {
		int currentPos = findPos(x);
		return isActive(currentPos) ? array[currentPos].element : null;
	}

	/**
	 * Return true if currentPos exists and is active.
	 * 
	 * @param currentPos
	 *            the result of a call to findPos.
	 * @return true if currentPos is active.
	 */
	private boolean isActive(int currentPos) {
		return array[currentPos] != null && array[currentPos].isActive;
	}

	/**
	 * 使哈希表逻辑上清空
	 */
	public void makeEmpty() {
		currentSize = 0;
		for (int i = 0; i < array.length; i++)
			array[i] = null;
	}

	/**
	 * A hash routine for String objects.
	 * 
	 * @param key
	 *            the String to hash.
	 * @param tableSize
	 *            the size of the hash table.
	 * @return the hash value.
	 */
	public static int hash(String key, int tableSize) {
		int hashVal = 0;

		for (int i = 0; i < key.length(); i++)
			hashVal = 37 * hashVal + key.charAt(i);

		hashVal %= tableSize;
		if (hashVal < 0)
			hashVal += tableSize;

		return hashVal;
	}

	private static final int DEFAULT_TABLE_SIZE = 11;

	/** The array of elements. */
	private HashEntry[] array; // The array of elements
	private int currentSize; // The number of occupied cells

	/**
	 * 私有方法：分配数组
	 * 
	 * @param arraySize
	 *            the size of the array.
	 */
	private void allocateArray(int arraySize) {
		array = new HashEntry[arraySize];
	}

	/**
	 * 私有方法：找到下一个素数
	 * 
	 * @param n
	 *            the starting number (must be positive).
	 * @return 大于等于n的素数
	 */
	private static int nextPrime(int n) {
		if (n % 2 == 0)
			n++;

		for (; !isPrime(n); n += 2)
			;

		return n;
	}

	/**
	 * 私有方法：判断是否是素数
	 * 
	 * @param n
	 *            the number to test.
	 * @return the result of the test.
	 */
	private static boolean isPrime(int n) {
		if (n == 2 || n == 3)
			return true;

		if (n == 1 || n % 2 == 0)
			return false;

		for (int i = 3; i * i <= n; i += 2)
			if (n % i == 0)
				return false;

		return true;
	}

	// Simple main
	public static void main(String[] args) {
		QuadraticProbingHashTable H = new QuadraticProbingHashTable();

		final int NUMS = 4000;
		final int GAP = 37;

		System.out.println("Checking... (no more output means success)");

		for (int i = GAP; i != 0; i = (i + GAP) % NUMS)
			H.insert(new MyInteger(i));
		for (int i = 1; i < NUMS; i += 2)
			H.remove(new MyInteger(i));

		for (int i = 2; i < NUMS; i += 2)
			if (((MyInteger) (H.find(new MyInteger(i)))).intValue() != i)
				System.out.println("Find fails " + i);

		for (int i = 1; i < NUMS; i += 2) {
			if (H.find(new MyInteger(i)) != null)
				System.out.println("OOPS!!! " + i);
		}
	}

}
