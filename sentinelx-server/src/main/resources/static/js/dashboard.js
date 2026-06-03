/**
 * Dashboard charts — polls /api/v1/nodes and renders aggregate CPU/RAM sparklines.
 */
(function () {
    'use strict';

    const CHART_COLOR_CPU = '#0dcaf0';
    const CHART_COLOR_RAM = '#0d6efd';
    const POLL_INTERVAL_MS = 30_000;

    let cpuChart = null;
    let ramChart = null;

    function buildChartConfig(label, color, labels, data) {
        return {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    label,
                    data,
                    borderColor: color,
                    backgroundColor: color + '22',
                    fill: true,
                    tension: 0.3,
                    pointRadius: 0,
                    borderWidth: 2,
                }]
            },
            options: {
                responsive: true,
                animation: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: ctx => ctx.parsed.y.toFixed(1) + '%'
                        }
                    }
                },
                scales: {
                    x: {
                        ticks: { color: '#888', maxTicksLimit: 8, font: { size: 10 } },
                        grid: { color: '#2e333d' }
                    },
                    y: {
                        min: 0,
                        max: 100,
                        ticks: { color: '#888', callback: v => v + '%', font: { size: 10 } },
                        grid: { color: '#2e333d' }
                    }
                }
            }
        };
    }

    async function fetchAndRender() {
        try {
            const resp = await fetch('/api/v1/nodes');
            if (!resp.ok) return;
            const nodes = await resp.json();

            // Collect last metric timestamps as x-axis labels (use node names as buckets)
            const labels = nodes.map(n => n.name);
            const cpuData = nodes.map(n => n.lastCpu ?? 0);
            const ramData = nodes.map(n => n.lastRam ?? 0);

            // Dashboard doesn't have per-node time-series endpoint, so show bar-style snapshot
            if (!cpuChart) {
                cpuChart = new Chart(document.getElementById('cpuChart'),
                    buildChartConfig('CPU %', CHART_COLOR_CPU, labels, cpuData));
                ramChart = new Chart(document.getElementById('ramChart'),
                    buildChartConfig('RAM %', CHART_COLOR_RAM, labels, ramData));
            } else {
                cpuChart.data.labels = labels;
                cpuChart.data.datasets[0].data = cpuData;
                cpuChart.update();

                ramChart.data.labels = labels;
                ramChart.data.datasets[0].data = ramData;
                ramChart.update();
            }
        } catch (e) {
            console.warn('Dashboard chart fetch failed:', e);
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        fetchAndRender();
        setInterval(fetchAndRender, POLL_INTERVAL_MS);
    });
})();
