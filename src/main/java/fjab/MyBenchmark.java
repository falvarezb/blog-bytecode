/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package fjab;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class MyBenchmark {

//    @Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @Fork(value = 1, warmups = 2)
//    @Warmup(iterations = 5)
//    @Measurement(iterations = 5)
//    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    public void testMethod() {
//        // place your benchmarked code here
//    }

    static LongBox longBox = new LongBox();
    static Box box = new Box();


//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @Fork(value = 2, warmups = 3)
//    @Warmup(iterations = 10)
//    @Measurement(iterations = 10)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testLocalVariable(Blackhole blackhole) {
        long j = 0;
        j += 1;
        blackhole.consume(j);
    }

//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @Fork(value = 2, warmups = 3)
//    @Warmup(iterations = 10)
//    @Measurement(iterations = 10)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testHeapVariable(Blackhole blackhole) {
        longBox.j = 0;
        longBox.j += 1;
        blackhole.consume(longBox.j);
    }

    //====================

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testMultipleArgument(Blackhole blackhole) {
        int j1 = 0;
        int j2 = 0;
        int j3 = 0;
        int j4 = 0;
        int j5 = 0;
        int j6 = 0;
        int j7 = 0;
        int j8 = 0;
        int j9 = 0;
        int j10 = 0;
        receiver1(j1,j2,j3,j4,j5,j6,j7,j8,j9,j10, blackhole);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    public void testSingleArgument(Blackhole blackhole) {
        box.j1 = 0;
        box.j2 = 0;
        box.j3 = 0;
        box.j4 = 0;
        box.j5 = 0;
        box.j6 = 0;
        box.j7 = 0;
        box.j8 = 0;
        box.j9 = 0;
        box.j10 = 0;
        receiver2(box, blackhole);
    }

    private void receiver1(int j1,int j2,int j3,int j4,int j5,int j6,int j7,int j8,int j9,int j10,Blackhole blackhole) {
        j1 += 1;
        j2 += 1;
        j3 += 1;
        j4 += 1;
        j5 += 1;
        j6 += 1;
        j7 += 1;
        j8 += 1;
        j9 += 1;
        j10 += 1;
        blackhole.consume(j1);
        blackhole.consume(j2);
        blackhole.consume(j3);
        blackhole.consume(j4);
        blackhole.consume(j5);
        blackhole.consume(j6);
        blackhole.consume(j7);
        blackhole.consume(j8);
        blackhole.consume(j9);
        blackhole.consume(j10);

    }

    private void receiver2(Box box, Blackhole blackhole) {
        box.j1 += 1;
        box.j2 += 1;
        box.j3 += 1;
        box.j4 += 1;
        box.j5 += 1;
        box.j6 += 1;
        box.j7 += 1;
        box.j8 += 1;
        box.j9 += 1;
        box.j10 += 1;
        blackhole.consume(box);
    }

}
