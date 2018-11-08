#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Number of ways to climb n stairs when it is possible to take 1,2 or 3 steps
at a time
"""

import timeit

#recursive solution
def count_combinations_recursive(n):
    if n == 1:
        return 1 #(1)
    elif n == 2:
        return 2 #(1,1), (2)
    elif n == 3:
        return 4 #(1,1,1), (2,1), (1,2), (3)
    return count_combinations_recursive(n-1) + count_combinations_recursive(n-2) + count_combinations_recursive(n-3)


#dynamic programming solution with bottom-up memoisation
def count_combinations_dp_bottom_up(n):
    results = [0] * n
    results[0] = 1
    results[1] = 2
    results[2] = 4
    for i in range(3, n):
        results[i] = results[i-1] + results[i-2] + results[i-3]
    return results[n-1]

#dynamic programming solution with top-down memoisation
def count_combinations_dp_top_down(n):
    results = [0] * n
    def loop(n):
        if results[n-1] == 0:
            if n == 1:
                results[n-1] = 1
            elif n == 2:
                results[n-1] = 2
            elif n == 3:
                results[n-1] = 4
            else:
                results[n-1] = loop(n-1) + loop(n-2) + loop(n-3)
        return results[n-1]
    return loop(n)


def enumerate_combinations_recursive(n):
    if n == 1:
        return [[1]]
    elif n == 2:
        return [[1, 1], [2]]
    elif n == 3:
        return [[1, 1, 1], [2, 1], [1, 2], [3]]
    return [i+[1] for i in enumerate_combinations_recursive(n-1)] + [i+[2] for i in enumerate_combinations_recursive(n-2)] + [i+[3] for i in enumerate_combinations_recursive(n-3)]

def enumerate_combinations_dp(n):
    results = [[[1]], [[1, 1], [2]], [[1, 1, 1], [2, 1], [1, 2], [3]]]
    for j in range(3, n):
        results.append([i+[1] for i in results[j-1]] + [i+[2] for i in results[j-2]] + [i+[3] for i in results[j-3]])
    return results[n-1]

n = 300

#print(timeit.timeit(lambda: count_combinations_recursive(n), number=1))
print(timeit.timeit(lambda: count_combinations_dp_bottom_up(n), number=10000))
print(timeit.timeit(lambda: count_combinations_dp_top_down(n), number=10000))

#print(count_combinations_dp_bottom_up(n))
#print(count_combinations_dp_top_down(n))
#print(count_combinations_recursive(n))
#print(enumerate_combinations_dp(n))
#print(enumerate_combinations_recursive(n))
