/* SentinelX — dashboard charts, polls /api/v1/nodes every 30s */
(function () {
    'use strict';

    const POLL_MS = 30_000;
    const C_CPU   = '#06b6d4';
    const C_RAM   = '#3b82f6';
    const GRID    = 'rgba(42,49,71,.8)';
    const TICK    = '#6b7a99';

    let cpuChart = null;
    let ramChart = null;

    function chartCfg(label, color, labels, data) {
        return {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    label,
                    data,
                    backgroundColor: color + '33',
                    borderColor: color,
                    borderWidth: 1.5,
                    borderRadius: 4,
                    borderSkipped: false,
                }]
            },
            options: {
                responsive: true,
                animation: { duration: 400 },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#1c2233',
                        borderColor: '#2a3147',
                        borderWidth: 1,
                        titleColor: '#94a3b8',
                        bodyColor: '#e2e8f0',
                        callbacks: { label: ctx => ' ' + ctx.parsed.y.toFixed(1) + '%' }
                    }
                },
                scales: {
                    x: {
                        ticks: { color: TICK, font: { size: 11 }, maxRotation: 0 },
                        grid: { color: GRID }
                    },
                    y: {
                        min: 0, max: 100,
                        ticks: { color: TICK, font: { size: 11 }, callback: v => v + '%' },
                        grid: { color: GRID }
                    }
                }
            }
        };
    }

    async function fetchAndRender() {
        try {
            const indicator = document.getElementById('refreshIndicator');
            if (indicator) indicator.style.display = 'flex';

            const resp = await fetch('/api/v1/nodes');
            if (!resp.ok) return;
            const nodes = await resp.json();

            const labels  = nodes.map(n => n.name);
            const cpuData = nodes.map(n => n.lastCpu ?? 0);
            const ramData = nodes.map(n => n.lastRam ?? 0);

            if (!cpuChart) {
                cpuChart = new Chart(document.getElementById('cpuChart'), chartCfg('CPU %', C_CPU, labels, cpuData));
                ramChart = new Chart(document.getElementById('ramChart'), chartCfg('RAM %', C_RAM, labels, ramData));
            } else {
                cpuChart.data.labels = labels;
                cpuChart.data.datasets[0].data = cpuData;
                cpuChart.update();
                ramChart.data.labels = labels;
                ramChart.data.datasets[0].data = ramData;
                ramChart.update();
            }

            if (indicator) setTimeout(() => { indicator.style.display = 'none'; }, 600);
        } catch (e) {
            console.warn('Dashboard chart fetch failed:', e);
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        fetchAndRender();
        setInterval(fetchAndRender, POLL_MS);
    });
})();
