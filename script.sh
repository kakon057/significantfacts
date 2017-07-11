#!/bin/bash

cd ./src

OUTPUT="../output"

rm -rf $OUTPUT/*
mkdir $OUTPUT

ALGO_NAME=( "Baseline" "BottomUp" "TopDown" "STopDown")

TUPLE_NUM=$5


for((d = $1 ; d <= $2 ; d++))
do
	for((m = $3 ; m <= $4 ; d++))
	do
		for((alg = 0 ; alg <= 3; alg++))
		do				
			echo starting ${ALGO_NAME[$alg]} ${d} ${m}	
			OUTPUT_FILE=$(echo ${ALGO_NAME[$alg]}_${i}_${d}_${m}.txt)
			javac -classpath $LIB/trove-3.0.3.jar: Tuple.java ${ALGO_NAME[$alg]}.java
			nohup java -classpath $LIB/trove-3.0.3.jar: -Xmx16G ${ALGO_NAME[$alg]} $d $m $5 > $OUTPUT/$OUTPUT_FILE
			echo DONE: ${ALGO_NAME[$alg]} ${d} ${m}	
		done
	done		
done


javac Dimension.java
nohup java -Xmx16G Dimension $1 $2 $3 $4


cd ../output

OUTPUT_FILE=$(echo gnuplot_script.sh)

mkdir images

echo "gnuplot -persist <<-EOFMarker" >> $OUTPUT_FILE
echo "set terminal postscript color solid enhanced font \"Helvetica,26\"" >> $OUTPUT_FILE
echo "set key right bottom" >> $OUTPUT_FILE
echo set "datafile separator \",\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set style line 1 lt rgb \"red\" lw 3 pt 2 ps 2" >> $OUTPUT_FILE
echo "set style line 2 lt rgb \"blue\" lw 3 pt 8 ps 2" >> $OUTPUT_FILE
echo "set style line 3 lt rgb \"#006400\" lw 3 pt 6 ps 2" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set ylabel \"Execution Time (ms)\"" >> $OUTPUT_FILE
echo "set xlabel \"Tuple ID\"" >> $OUTPUT_FILE
echo "set logscale y" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set format y \"10^{%T}\"" >> $OUTPUT_FILE
echo "set xtics (200000, 400000, 600000, 800000)" >> $OUTPUT_FILE
echo "set xrange [200000:1000000]" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((d = $4 ; d <= $4 ; d++))
	do
		for((c_approx = $c2; c_approx <= $c2; c_approx++))
		do	
			FILE=$(echo ${i}_${d}.eps)					
			echo "set output \"./images/TUPLE_VS_TIME_${FILE}\"" >> $OUTPUT_FILE
			echo "plot \"Baseline_${i}_${d}_${c_approx}.txt\" u 1:2 every 10 with linespoints ls 1 title \"Baseline\", \"FilterThenVerify_${i}_${d}_1.txt\" u 1:2 every 10 with linespoints ls 2 title \"FilterThenVerifyExact\", \"FilterThenVerify_${i}_${d}_${c_approx}.txt\" u 1:2 every 10 with linespoints ls 3 title \"FilterThenVerifyApprox\"" >> $OUTPUT_FILE
		done
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Number of Comparisons\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((d = $4 ; d <= $4 ; d++))
	do
		for((c_approx = $c2; c_approx <= $c2; c_approx++))
		do	
			FILE=$(echo ${i}_${d}.eps)					
			echo "set output \"./images/TUPLE_VS_COM_${FILE}\"" >> $OUTPUT_FILE
			echo "plot \"Baseline_${i}_${d}_${c_approx}.txt\" u 1:3 every 10 with linespoints ls 1 title \"Baseline\", \"FilterThenVerify_${i}_${d}_1.txt\" u 1:3 every 10 with linespoints ls 2 title \"FilterThenVerifyExact\", \"FilterThenVerify_${i}_${d}_${c_approx}.txt\" u 1:3 every 10 with linespoints ls 3 title \"FilterThenVerifyApprox\"" >> $OUTPUT_FILE
		done
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Cumulative Execution Time (ms)\"" >> $OUTPUT_FILE
echo "set xlabel \"Number of Dimensions\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set xtics 1" >> $OUTPUT_FILE
echo "set xrange [$3:$4]" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((c_approx = $c2; c_approx <= $c2; c_approx++))
	do	
		FILE=$(echo ${i}.eps)					
		echo "set output \"./images/DIM_VS_TIME_${FILE}\"" >> $OUTPUT_FILE
		echo "plot \"Baseline_Dimension_${i}_${c_approx}.txt\" u 1:2 with linespoints ls 1 title \"Baseline\", \"FilterThenVerify_Dimension_${i}_1.txt\" u 1:2 with linespoints ls 2 title \"FilterThenVerifyExact\", \"FilterThenVerify_Dimension_${i}_${c_approx}.txt\" u 1:2 with linespoints ls 3 title \"FilterThenVerifyApprox\"" >> $OUTPUT_FILE
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Number of Comparisons\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((c_approx = $c2; c_approx <= $c2; c_approx++))
	do	
		FILE=$(echo ${i}.eps)				
		echo "set output \"./images/DIM_VS_COM_${FILE}\"" >> $OUTPUT_FILE
		echo "plot \"Baseline_Dimension_${i}_${c_approx}.txt\" u 1:3 with linespoints ls 1 title \"Baseline\", \"FilterThenVerify_Dimension_${i}_1.txt\" u 1:3 with linespoints ls 2 title \"FilterThenVerifyExact\", \"FilterThenVerify_Dimension_${i}_${c_approx}.txt\" u 1:3 with linespoints ls 3 title \"FilterThenVerifyApprox\"" >> $OUTPUT_FILE
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Cumulative Execution Time (ms)\"" >> $OUTPUT_FILE
echo "set xlabel \"Window Size\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set xtics (\"400\" 1, \"800\" 2, \"1600\" 3, \"3200\" 4)" >> $OUTPUT_FILE
echo "set xrange [1:4]" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((d = $4 ; d <= $4 ; d++))
	do
		for((c_approx = $c2; c_approx <= $c2; c_approx++))
		do
			FILE=$(echo ${i}_${d}.eps)					
			echo "set output \"./images/WINDOW_VS_TIME_${FILE}\"" >> $OUTPUT_FILE
			echo "plot \"BaselineSW_${i}_${d}_${c_approx}.txt\" u 1:3 with linespoints ls 1 title \"BaselineSW\", \"FilterThenVerifySW_${i}_${d}_1.txt\" u 1:3 with linespoints ls 2 title \"FilterThenVerifyExactSW\", \"FilterThenVerifySW_${i}_${d}_${c_approx}.txt\" u 1:3 with linespoints ls 3 title \"FilterThenVerifyApproxSW\"" >> $OUTPUT_FILE
		done
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Number of Comparisons\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((d = $4 ; d <= $4 ; d++))
	do
		for((c_approx = $c2; c_approx <= $c2; c_approx++))
		do
			FILE=$(echo ${i}_${d}.eps)				
			echo "set output \"./images/WINDOW_VS_COM_${FILE}\"" >> $OUTPUT_FILE
			echo "plot \"BaselineSW_${i}_${d}_${c_approx}.txt\" u 1:4 with linespoints ls 1 title \"BaselineSW\", \"FilterThenVerifySW_${i}_${d}_1.txt\" u 1:4 with linespoints ls 2 title \"FilterThenVerifyExactSW\", \"FilterThenVerifySW_${i}_${d}_${c_approx}.txt\" u 1:4 with linespoints ls 3 title \"FilterThenVerifyApproxSW\"" >> $OUTPUT_FILE
		done
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Cumulative Execution Time (ms)\"" >> $OUTPUT_FILE
echo "set xlabel \"Number of Dimensions\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

echo "set xtics 1" >> $OUTPUT_FILE
echo "set xrange [$3:$4]" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((c_approx = $c2; c_approx <= $c2; c_approx++))
	do	
		FILE=$(echo ${i}.eps)					
		echo "set output \"./images/DIMSW_VS_TIME_${FILE}\"" >> $OUTPUT_FILE
		echo "plot \"BaselineSW_Dimension_${i}_${c_approx}.txt\" u 1:2 with linespoints ls 1 title \"BaselineSW\", \"FilterThenVerifySW_Dimension_${i}_1.txt\" u 1:2 with linespoints ls 2 title \"FilterThenVerifyExactSW\", \"FilterThenVerifySW_Dimension_${i}_${c_approx}.txt\" u 1:2 with linespoints ls 3 title \"FilterThenVerifyApproxSW\"" >> $OUTPUT_FILE
	done
done

echo >> $OUTPUT_FILE
echo "set ylabel \"Number of Comparisons\"" >> $OUTPUT_FILE
echo >> $OUTPUT_FILE

for((i = $1 ; i <= $2 ; i++))
do
	for((c_approx = $c2; c_approx <= $c2; c_approx++))
	do	
		FILE=$(echo ${i}.eps)				
		echo "set output \"./images/DIMSW_VS_COM_${FILE}\"" >> $OUTPUT_FILE
		echo "plot \"BaselineSW_Dimension_${i}_${c_approx}.txt\" u 1:3 with linespoints ls 1 title \"BaselineSW\", \"FilterThenVerifySW_Dimension_${i}_1.txt\" u 1:3 with linespoints ls 2 title \"FilterThenVerifyExactSW\", \"FilterThenVerifySW_Dimension_${i}_${c_approx}.txt\" u 1:3 with linespoints ls 3 title \"FilterThenVerifyApproxSW\"" >> $OUTPUT_FILE
	done
done

echo "EOFMarker" >> $OUTPUT_FILE

chmod 777 gnuplot_script.sh
./gnuplot_script.sh
cd ..