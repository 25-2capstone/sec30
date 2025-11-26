// 플레이리스트 생성 모달 표시
function showCreateModal() {
    const title = prompt('플레이리스트 이름을 입력하세요:');
    if (!title) return;

    const description = prompt('설명을 입력하세요 (선택사항):');

    fetch('/playlists', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title: title,
            description: description || '',
            coverImage: 'https://via.placeholder.com/300'
        })
    })
    .then(response => response.json())
    .then(data => {
        alert('플레이리스트가 생성되었습니다!');
        location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('플레이리스트 생성에 실패했습니다.');
    });
}

// 플레이리스트 삭제
function deletePlaylist(playlistId) {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    fetch(`/playlists/${playlistId}`, {
        method: 'DELETE'
    })
    .then(() => {
        alert('플레이리스트가 삭제되었습니다.');
        location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('삭제에 실패했습니다.');
    });
}
// 플레이리스트 만들기
function createPlaylist() {
    const title = prompt('플레이리스트 이름을 입력하세요:');
    if (!title) return;

    const description = prompt('설명을 입력하세요 (선택사항):');

    fetch('/playlists', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title: title,
            description: description || '',
            coverImage: 'https://via.placeholder.com/300'
        })
    })
    .then(response => response.json())
    .then(data => {
        alert('플레이리스트가 생성되었습니다!');
        window.location.href = '/playlists/' + data.id;
    })
    .catch(error => {
        console.error('Error:', error);
        alert('플레이리스트 생성에 실패했습니다.');
    });
}

// 프로그레스 바 클릭 처리
document.addEventListener('DOMContentLoaded', function() {
    const progressBar = document.querySelector('.progress-bar');
    if (progressBar) {
        progressBar.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const width = rect.width;
            const percent = (x / width) * 100;

            const progressFilled = this.querySelector('.progress-filled');
            if (progressFilled) {
                progressFilled.style.width = percent + '%';
            }
        });
    }

    // 볼륨 바 클릭 처리
    const volumeBar = document.querySelector('.volume-bar');
    if (volumeBar) {
        volumeBar.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const width = rect.width;
            const percent = (x / width) * 100;

            const volumeFilled = this.querySelector('.volume-filled');
            if (volumeFilled) {
                volumeFilled.style.width = percent + '%';
            }
        });
    }
});

