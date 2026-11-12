document.addEventListener('DOMContentLoaded', async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const shortCode = urlParams.get('code');

  if (!shortCode) {
    alert('No short code provided in URL parameter. Redirecting to home.');
    window.location.href = '/';
    return;
  }

  const statTotalClicks = document.getElementById('statTotalClicks');
  const statStatus = document.getElementById('statStatus');
  const statCreatedAt = document.getElementById('statCreatedAt');
  const statExpiresAt = document.getElementById('statExpiresAt');
  const targetUrlSubtitle = document.getElementById('targetUrlSubtitle');
  const testShortLinkBtn = document.getElementById('testShortLinkBtn');
  const clickLogsBody = document.getElementById('clickLogsBody');

  try {
    const res = await fetch(`/api/v1/urls/${encodeURIComponent(shortCode)}/analytics`);
    if (!res.ok) {
      alert('Could not find analytics for code: ' + shortCode);
      window.location.href = '/';
      return;
    }

    const data = await res.json();

    // Populate Key Metrics
    statTotalClicks.innerText = data.totalClicks.toLocaleString();
    statStatus.innerHTML = data.expired
      ? `<span class="badge badge-expired">Expired</span>`
      : `<span class="badge badge-active">Active</span>`;

    statCreatedAt.innerText = new Date(data.createdAt).toLocaleDateString(undefined, {
      month: 'short', day: 'numeric', year: 'numeric'
    });

    statExpiresAt.innerText = data.expiresAt
      ? new Date(data.expiresAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })
      : 'Never';

    targetUrlSubtitle.innerHTML = `
      <strong>Destination:</strong> <a href="${data.originalUrl}" target="_blank" style="color: #38bdf8;">${data.originalUrl}</a><br>
      <strong>Short Link:</strong> <a href="${data.shortUrl}" target="_blank" style="color: #a78bfa;">${data.shortUrl}</a>
    `;

    testShortLinkBtn.href = data.shortUrl;

    // Render Timeline Chart
    renderTimelineChart(data.clicksOverTime);

    // Render Browser Chart
    renderPieChart('browserChart', data.clicksByBrowser, ['#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6']);

    // Render OS Chart
    renderPieChart('osChart', data.clicksByOperatingSystem, ['#6366f1', '#06b6d4', '#14b8a6', '#f43f5e', '#a855f7']);

    // Render Recent Click Logs
    renderLogs(data.recentClicks);

  } catch (err) {
    console.error('Error fetching analytics:', err);
    alert('Failed to load analytics data.');
  }

  function renderTimelineChart(clicksOverTime) {
    const ctx = document.getElementById('timelineChart').getContext('2d');
    const labels = Object.keys(clicksOverTime || {});
    const counts = Object.values(clicksOverTime || {});

    new Chart(ctx, {
      type: 'line',
      data: {
        labels: labels.length > 0 ? labels : ['Today'],
        datasets: [{
          label: 'Clicks',
          data: counts.length > 0 ? counts : [0],
          borderColor: '#38bdf8',
          backgroundColor: 'rgba(56, 189, 248, 0.15)',
          fill: true,
          tension: 0.3,
          borderWidth: 2,
          pointBackgroundColor: '#38bdf8'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          x: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#94a3b8' } },
          y: {
            beginAtZero: true,
            ticks: { precision: 0, color: '#94a3b8' },
            grid: { color: 'rgba(255, 255, 255, 0.05)' }
          }
        }
      }
    });
  }

  function renderPieChart(canvasId, dataMap, colors) {
    const ctx = document.getElementById(canvasId).getContext('2d');
    const labels = Object.keys(dataMap || {});
    const counts = Object.values(dataMap || {});

    if (labels.length === 0) {
      labels.push('No data');
      counts.push(1);
      colors = ['rgba(255, 255, 255, 0.1)'];
    }

    new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: counts,
          backgroundColor: colors,
          borderColor: '#1e293b',
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: { color: '#cbd5e1', boxWidth: 12 }
          }
        }
      }
    });
  }

  function renderLogs(logs) {
    if (!logs || logs.length === 0) {
      clickLogsBody.innerHTML = `
        <tr>
          <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">
            No clicks recorded yet. Click the short link to generate analytics!
          </td>
        </tr>`;
      return;
    }

    clickLogsBody.innerHTML = logs.map(log => {
      const dateStr = new Date(log.clickedAt).toLocaleString();
      return `
        <tr>
          <td style="color: #cbd5e1; font-size: 0.85rem;">${dateStr}</td>
          <td><code>${log.ipAddress}</code></td>
          <td><span class="badge" style="background: rgba(255,255,255,0.1); color: #fff;">${log.deviceType}</span></td>
          <td>${log.browser}</td>
          <td>${log.operatingSystem}</td>
          <td style="color: var(--text-muted); font-size: 0.85rem;">${log.referer}</td>
        </tr>
      `;
    }).join('');
  }
});
