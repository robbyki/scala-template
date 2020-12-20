def isSorted[A](as: Array[A], ordering: (A, A) => Boolean): Boolean = {
  @annotation.tailrec
  def go(n: Int): Boolean =
    if (n >= as.length - 1) true
    else if (!ordering(as(n), as(n + 1))) false
    else go(n + 1)
  go(0)
}

println(isSorted(Array(1, 7, 2, 2), (x: Int, y: Int) => x < y))
println(isSorted(Array(1, 2, 3, 7), (x: Int, y: Int) => x < y))
println(isSorted(Array(1, 5, 3, 7), (x: Int, y: Int) => x < y))
