# chart_matrix_comparison.py

import matplotlib.pyplot as plt

# ✅ Replace with your actual timing values (in seconds)
time_conventional_mn = 0.000129
time_conventional_nm = 0.000127
time_strassen_mn = 0.000323
time_strassen_nm = 0.000312

labels = [
    'Conventional M × N',
    'Conventional N × M',
    'Strassen M × N',
    'Strassen N × M'
]

times_ms = [
    time_conventional_mn * 1000,
    time_conventional_nm * 1000,
    time_strassen_mn * 1000,
    time_strassen_nm * 1000
]

plt.figure(figsize=(10, 5))
bars = plt.bar(labels, times_ms, color=['#4caf50', '#4caf50', '#2196f3', '#2196f3'])

for bar, val in zip(bars, times_ms):
    plt.text(bar.get_x() + bar.get_width() / 2, val + 0.005, f'{val:.4f} ms',
             ha='center', va='bottom')

plt.ylabel('Execution Time (ms)')
plt.title('Matrix Multiplication Time Comparison: Conventional vs. Strassen')
plt.grid(axis='y', linestyle='--', alpha=0.7)
plt.tight_layout()
plt.savefig('matrix_multiplication_comparison.png')
plt.show()
