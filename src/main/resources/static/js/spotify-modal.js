// spotify-modal.js
// 전역함수들을 window에 노출하여 기존 템플릿의 inline onclick을 그대로 유지합니다.
(function(){
  const log = (...args) => { try{ console.debug('[spotify-modal]', ...args); } catch(e){} };

  window.logout = function(){
    if (confirm('로그아웃 하시겠습니까?')) { window.location.href = '/logout'; }
  };

  window.toggleFavorite = function(trackId, btn){
    fetch('/favorite', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'trackId=' + encodeURIComponent(trackId)
    }).then(r => r.json()).then(j => {
      if (!j.ok) { alert(j && j.message ? j.message : '실패'); return; }
      if (j.added) { btn.textContent = '♥'; btn.classList.add('added'); }
      else { btn.textContent = '♡'; btn.classList.remove('added'); }
    }).catch(e => { alert('요청 실패'); });
  };

  window.playTrack = function(previewUrl){
    try{
      var player = document.getElementById('global-player');
      if (previewUrl && previewUrl !== 'null' && previewUrl !== ''){
        player.src = previewUrl;
        player.play().catch(e => { console.log('play failed', e); alert('브라우저 자동재생 제한: 재생 버튼을 다시 눌러주세요'); });
      } else {
        var AudioCtx = window.AudioContext ? window.AudioContext : (window.webkitAudioContext ? window.webkitAudioContext : null);
        if (!AudioCtx) return;
        var ctx = new AudioCtx();
        var o = ctx.createOscillator(); var g = ctx.createGain();
        o.type = 'sine'; o.frequency.value = 440;
        o.connect(g); g.connect(ctx.destination);
        g.gain.setValueAtTime(0.0001, ctx.currentTime);
        g.gain.exponentialRampToValueAtTime(0.08, ctx.currentTime + 0.01);
        o.start(); g.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + 0.8); o.stop(ctx.currentTime + 0.82);
      }
    } catch(err){ console.error('playTrack error', err); }
  };

  window.handlePlay = function(btn){
    var preview = btn.getAttribute('data-preview');
    var trackId = btn.getAttribute('data-trackid');
    if (preview && preview !== '') window.playTrack(preview);
    else if (trackId) window.openSpotifyEmbed(trackId);
    else alert('재생할 수 없습니다.');
  };

  function createModalElements(){
    var modal = document.createElement('div'); modal.id = 'spotify-modal'; modal.className = 'spotify-modal';
    var inner = document.createElement('div'); inner.className = 'spotify-modal__inner';
    var header = document.createElement('div'); header.className = 'spotify-modal__header';
    var title = document.createElement('div'); title.className = 'spotify-modal__title'; title.textContent = 'Spotify Player';
    var controlsWrap = document.createElement('div'); controlsWrap.style.display='flex'; controlsWrap.style.gap='8px'; controlsWrap.style.alignItems='center';
    var fullBtn = document.createElement('button'); fullBtn.className = 'spotify-modal__close'; fullBtn.textContent = '전체';
    fullBtn.addEventListener('click', function(){ if (inner.classList.contains('full')){ inner.classList.remove('full'); fullBtn.textContent='전체'; } else { inner.classList.add('full'); fullBtn.textContent='줄이기'; } });
    var closeBtn = document.createElement('button'); closeBtn.className = 'spotify-modal__close'; closeBtn.textContent = '닫기'; closeBtn.addEventListener('click', closeSpotifyEmbed);
    controlsWrap.appendChild(fullBtn); controlsWrap.appendChild(closeBtn);
    header.appendChild(title); header.appendChild(controlsWrap);
    var playerWrap = document.createElement('div'); playerWrap.className = 'spotify-modal__player';
    var frame = document.createElement('iframe'); frame.id = 'spotify-embed-frame'; frame.className = 'spotify-modal__iframe'; frame.setAttribute('allow','autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture');
    playerWrap.appendChild(frame);
    inner.appendChild(header); inner.appendChild(playerWrap); modal.appendChild(inner);
    // backdrop click
    modal.addEventListener('click', function(e){ if (e.target === modal) closeSpotifyEmbed(); });
    // esc key (bind once)
    if (!window._spotifyModalKeyboardBound){ window._spotifyModalKeyboardBound = true; document.addEventListener('keydown', function(ev){ if (ev.key === 'Escape') closeSpotifyEmbed(); }); }
    document.body.appendChild(modal);
    return { modal: modal, inner: inner, frame: frame };
  }

  window.openSpotifyEmbed = function(trackId){
    var url = 'https://open.spotify.com/embed/track/' + trackId;
    var modal = document.getElementById('spotify-modal');
    var frame = document.getElementById('spotify-embed-frame');
    var inner = modal ? modal.querySelector('.spotify-modal__inner') : null;
    if (!modal){
      var el = createModalElements(); modal = el.modal; inner = el.inner; frame = el.frame;
      // 기본 전체 모드
      inner.classList.add('full');
    } else if (!frame){
      var innerDiv = modal.querySelector('.spotify-modal__inner');
      var playerWrap = modal.querySelector('.spotify-modal__player');
      if (!playerWrap) { playerWrap = document.createElement('div'); playerWrap.className = 'spotify-modal__player'; innerDiv.appendChild(playerWrap); }
      frame = document.createElement('iframe'); frame.id='spotify-embed-frame'; frame.className='spotify-modal__iframe'; frame.setAttribute('allow','autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture'); playerWrap.appendChild(frame); innerDiv.classList.add('full');
    }
    try { if (frame) frame.src = url; } catch(e){ console.error('Failed to set iframe src', e); }
    modal.classList.add('open');
  };

  window.closeSpotifyEmbed = function(){
    var m = document.getElementById('spotify-modal'); var f = document.getElementById('spotify-embed-frame');
    if (f) f.src = '';
    if (m) m.classList.remove('open');
  };

})();
