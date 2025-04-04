# chart_matrix_times.py
import matplotlib.pyplot as plt

labels = ['Conventional (Total)', 'Strassen M×N', 'Strassen N×M']
times_ms = [22.41, 0.12, 0.41]

plt.figure(figsize=(8, 5))
bars = plt.bar(labels, times_ms)

for bar, val in zip(bars, times_ms):
    plt.text(bar.get_x() + bar.get_width()/2, bar.get_height(), f'{val:.2f} ms', ha='center', va='bottom')

plt.ylabel('Time (ms)')
plt.title('Matrix Multiplication: Conventional vs Strassen')
plt.grid(axis='y')
plt.tight_layout()
plt.savefig('matrix_multiplication_times.png')
plt.show()
