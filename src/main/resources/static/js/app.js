document.addEventListener('DOMContentLoaded', () => {
  const shortenForm = document.getElementById('shortenForm');
  const originalUrlInput = document.getElementById('originalUrl');
  const customAliasInput = document.getElementById('customAlias');
  const expirationDaysSelect = document.getElementById('expirationDays');
  const submitBtn = document.getElementById('submitBtn');

  const resultBox = document.getElementById('resultBox');
  const shortLinkAnchor = document.getElementById('shortLinkAnchor');
  const copyBtn = document.getElementById('copyBtn');
  const analyticsBtn = document.getElementById('analyticsBtn');

  const urlsTableBody = document.getElementById('urlsTableBody');
  const refreshBtn = document.getElementById('refreshBtn');

  // Load existing URLs on startup
  loadUrls();

  refreshBtn.addEventListener('click', loadUrls);

  // Form submission handler
  shortenForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    submitBtn.disabled = true;
    submitBtn.innerText = 'Creating...';

    const payload = {
      originalUrl: originalUrlInput.value.trim(),
      customAlias: customAliasInput.value.trim() || null,
      expirationDays: expirationDaysSelect.value ? parseInt(expirationDaysSelect.value, 10) : null
    };

    try {
      const response = await fetch('/api/v1/urls/shorten', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (!response.ok) {
        const errorMsg = data.validationErrors
          ? Object.values(data.validationErrors).join('\n')
          : (data.message || 'Error creating short URL');
        alert('Validation Error: ' + errorMsg);
        return;
      }

      // Show success box
      shortLinkAnchor.href = data.shortUrl;
      shortLinkAnchor.innerText = data.shortUrl;
      analyticsBtn.href = `/analytics.html?code=${data.shortCode}`;
      resultBox.style.display = 'block';

      // Clear form inputs
      customAliasInput.value = '';
      expirationDaysSelect.value = '';

      // Reload URL list table
      loadUrls();
    } catch (err) {
      console.error(err);
      alert('Network or server error while creating short link.');
    } finally {
      submitBtn.disabled = false;
      submitBtn.innerText = 'Shorten URL';
    }
  });

  // Copy to clipboard
  copyBtn.addEventListener('click', () => {
    const textToCopy = shortLinkAnchor.innerText;
    navigator.clipboard.writeText(textToCopy).then(() => {
      const originalText = copyBtn.innerText;
      copyBtn.innerText = 'Copied!';
      setTimeout(() => {
        copyBtn.innerText = originalText;
      }, 2000);
    });
  });

  // Fetch and display active URLs
  async function loadUrls() {
    try {
      const response = await fetch('/api/v1/urls');
      if (!response.ok) return;

      const urls = await response.json();
      if (!urls || urls.length === 0) {
        urlsTableBody.innerHTML = `
          <tr>
            <td colspan="6" style="text-align: center; color: var(--text-muted); padding: 2rem;">
              No short URLs created yet. Use the form above to shorten your first link!
            </td>
          </tr>`;
        return;
      }

      urlsTableBody.innerHTML = urls.map(item => {
        const statusBadge = item.expired
          ? `<span class="badge badge-expired">Expired</span>`
          : `<span class="badge badge-active">Active</span>`;

        const createdDate = new Date(item.createdAt).toLocaleDateString(undefined, {
          month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
        });

        const shortUrl = item.shortUrl;
        const truncatedTarget = item.originalUrl.length > 45
          ? item.originalUrl.substring(0, 45) + '...'
          : item.originalUrl;

        return `
          <tr>
            <td>
              <a href="${shortUrl}" target="_blank" style="color: #38bdf8; font-weight: 700; text-decoration: none;">
                /${item.shortCode}
              </a>
            </td>
            <td>
              <a href="${item.originalUrl}" target="_blank" title="${item.originalUrl}" style="color: #cbd5e1; text-decoration: none;">
                ${truncatedTarget}
              </a>
            </td>
            <td><strong>${item.totalClicks}</strong></td>
            <td>${statusBadge}</td>
            <td style="color: var(--text-muted); font-size: 0.8rem;">${createdDate}</td>
            <td>
              <div style="display: flex; gap: 0.5rem;">
                <a href="/analytics.html?code=${item.shortCode}" class="btn btn-secondary btn-sm">Analytics</a>
                <button class="btn btn-secondary btn-sm" onclick="copyText('${shortUrl}')">Copy</button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    } catch (e) {
      console.error('Error fetching URLs:', e);
    }
  }

  window.copyText = function(text) {
    navigator.clipboard.writeText(text).then(() => {
      alert('Copied short link: ' + text);
    });
  };
});
