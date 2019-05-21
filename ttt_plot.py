#!/usr/bin/env python3

import subprocess
import matplotlib.pyplot as plt
import numpy as np
from sklearn.linear_model import LinearRegression

def read_csv(folder, op):
	x = []
	min_max = 20000
	for i in range(1, n + 1):
		f = open(f"{folder}/{i}.csv")
		out = f.read()
		f.close()
		for line in out.split('\n'):
			if line == '':
				continue
			time, val = [float(x) for x in line.split(',')]
			if op:
				val *= -1
			if val >= target:
				x.append(time)
				break
			if time >= 300.0:
				min_max = min(min_max, val)
	x.sort()
	print(min_max)
	y = [(0.5 + i) / n for i in range(len(x))]
	return np.array(x), np.array(y)

n = 200
target = 10000
target = 10700
target = 10900

def plot_ttt(folder, tipo, op = False):
	x, y = read_csv(folder, op)

	if (len(x) >= 2):
		model = LinearRegression().fit(-np.log(1 - y).reshape(-1, 1), x)
		mi = model.intercept_ 
		l = model.coef_[0]
		print(mi, l)

	plt.ylim((-0.05, 1.05))
	if (len(x) >= 2):
		plt.plot(x, 1 - np.exp(-(x - mi) / l), linestyle='dashed', color='gray')
	plt.plot(x, y, marker='+', linestyle='none',label=tipo)

plot_ttt("ttt_data_ga", "Algoritmo Genético")
plot_ttt("ttt_data_tabu", "Busca Tabu")
plot_ttt("ttt_data_grasp", "GRASP", True)
plt.ylabel("probabilidade")
plt.xlabel(f"tempo (s)")
plt.legend()
plt.show()

