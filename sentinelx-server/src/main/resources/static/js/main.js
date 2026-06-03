/* SentinelX — global UI helpers */
(function () {
    'use strict';

    /* Flash message auto-dismiss */
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.sx-flash').forEach(function (el) {
            var btn = el.querySelector('.sx-flash-dismiss');
            if (btn) {
                btn.addEventListener('click', function () {
                    el.style.transition = 'opacity .3s';
                    el.style.opacity = '0';
                    setTimeout(function () { el.remove(); }, 300);
                });
            }
            /* auto-dismiss success messages after 4s */
            if (el.classList.contains('sx-flash-success')) {
                setTimeout(function () {
                    el.style.transition = 'opacity .4s';
                    el.style.opacity = '0';
                    setTimeout(function () { el.remove(); }, 400);
                }, 4000);
            }
        });

        /* Table search filter */
        var searchInput = document.getElementById('sxTableSearch');
        if (searchInput) {
            searchInput.addEventListener('input', function () {
                var q = this.value.toLowerCase();
                document.querySelectorAll('[data-search-row]').forEach(function (row) {
                    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
                });
            });
        }

        /* Token visibility toggle */
        window.sxToggleToken = function (fieldId, iconId) {
            var f = document.getElementById(fieldId);
            var icon = document.getElementById(iconId);
            if (!f) return;
            if (f.type === 'password') {
                f.type = 'text';
                if (icon) icon.className = 'bi bi-eye-slash';
            } else {
                f.type = 'password';
                if (icon) icon.className = 'bi bi-eye';
            }
        };

        /* Token copy */
        window.sxCopyToken = function (fieldId) {
            var f = document.getElementById(fieldId);
            if (!f) return;
            navigator.clipboard.writeText(f.value).then(function () {
                var btn = document.getElementById('copyBtn');
                if (btn) {
                    var orig = btn.innerHTML;
                    btn.innerHTML = '<i class="bi bi-check-lg"></i>';
                    btn.style.color = 'var(--sx-green)';
                    setTimeout(function () {
                        btn.innerHTML = orig;
                        btn.style.color = '';
                    }, 1800);
                }
            });
        };
    });
})();
