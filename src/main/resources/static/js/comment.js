const token = localStorage.getItem('access_token');

/* 1. 댓글 새로고침 */
function refreshComments(playlistId) {
    console.log('🔄 refreshComments:', playlistId);

    $.ajax({
        url: `/playlist/${playlistId}/comments/list`,
        type: 'GET',
        dataType: 'html',
        success: function(result) {
            console.log('✅ Fragment received');
            $('#comments-container').replaceWith(result);
        },
        error: function(xhr, status, error) {
            console.error('Refresh error:', error);
            alert('댓글 목록 불러오기 실패');
        }
    });
}

/* 2. 초기화 */
function initializeCommentListeners() {
    console.log('🔧 Initializing comment listeners...');

    const commentCreateButton = document.getElementById('comment-create-btn');

    if (!commentCreateButton) {
        console.error('❌ comment-create-btn NOT FOUND!');
        return;
    }

    console.log('✅ comment-create-btn FOUND!');

    commentCreateButton.addEventListener('click', function(event) {
        console.log('📝 Comment button clicked');
        event.preventDefault();

        const playlistId = document.getElementById('playlist-id')?.value;
        const commentContent = document.getElementById('comment-content')?.value;

        if (!commentContent || commentContent.trim() === "") {
            alert("공백 또는 입력하지 않은 부분이 있습니다.");
            return;
        }

        $.ajax({
            url: `/playlist/${playlistId}/comments`,
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                comment: commentContent
            }),
            beforeSend: function(xhr) {
                if (token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            },
            success: function(response) {
                console.log('✅ Comment created:', response);
                alert('등록 완료되었습니다.');
                document.getElementById('comment-content').value = '';
                refreshComments(playlistId);
            },
            error: function(xhr) {
                console.error('❌ Create error:', xhr.responseText);
                alert('등록 실패했습니다.');
            }
        });
    });
}

/* 3. 이벤트 위임 - 삭제, 대댓글 토글, 대댓글 작성만 처리 */
document.addEventListener('click', function(e) {
    const playlistId = document.getElementById('playlist-id')?.value;

    // 삭제 버튼
    if (e.target.classList.contains('comment-delete-btn')) {
        console.log('🗑️ Delete clicked');
        e.stopPropagation();

        const commentId = e.target.getAttribute('data-reply-id');

        if (confirm('선택하신 댓글을 삭제할까요?')) {
            $.ajax({
                url: `/playlist/${playlistId}/comments/${commentId}`,
                type: 'DELETE',
                beforeSend: function(xhr) {
                    if (token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                    }
                },
                success: function() {
                    console.log('✅ Comment deleted');
                    alert('삭제되었습니다.');
                    refreshComments(playlistId);
                },
                error: function(xhr) {
                    console.error('❌ Delete error:', xhr.responseText);
                    alert('삭제 실패했습니다.');
                }
            });
        }
    }

    // 대댓글 토글
    if (e.target.classList.contains('reply-toggle-btn')) {
        console.log('💬 Reply toggle clicked');
        e.stopPropagation();

        const container = e.target.closest('.comment-container');
        const formContainer = container?.querySelector('.reply-form-container');

        if (formContainer) {
            formContainer.style.display = formContainer.style.display === 'none' ? 'block' : 'none';
        }
    }

    if (e.target.classList.contains('comment-update-btn')) {
        console.log('✏️ Update clicked');
        e.stopPropagation();  // ← 버블링 방지

        const commentId = e.target.getAttribute('data-reply-id');
        openCommentUpdatePopup(commentId, playlistId);
    }
}, true);

/* 4. 대댓글 작성 */
document.addEventListener('submit', function(e) {
    if (e.target.classList.contains('reply-form')) {
        e.preventDefault();

        const playlistId = document.getElementById('playlist-id').value;
        let parentId = e.target.querySelector('input[name="parent-id"]')?.value;

        if (!parentId || parentId === 'undefined') {
            parentId = e.target.querySelector('[data-parent-id]')?.getAttribute('data-parent-id');
        }
        if (!parentId || parentId === 'undefined') {
            parentId = e.target.getAttribute('data-parent-id');
        }

        const content = e.target.querySelector('textarea')?.value;

        console.log('📝 Reply form submitted', { playlistId, parentId, content });

        if (!parentId || parentId === 'undefined') {
            alert('부모 댓글 ID를 찾을 수 없습니다.');
            return;
        }

        if (!content || !content.trim()) {
            alert('대댓글을 입력해주세요');
            return;
        }

        $.ajax({
            url: `/playlist/${playlistId}/comments/${parentId}/reply`,
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                comment: content
            }),
            beforeSend: function(xhr) {
                if (token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                }
            },
            success: function(response) {
                console.log('✅ Reply created:', response);
                alert('대댓글 작성됨');
                e.target.querySelector('textarea').value = '';
                refreshComments(playlistId);
            },
            error: function(xhr) {
                console.error('❌ Reply error:', xhr.responseText);
                alert('작성 실패');
            }
        });
    }
});

/* 5. 수정 모달 - onclick에서 직접 호출 */
function handleEscKey(event) {
    if (event.key === 'Escape') {
        closeCommentUpdatePopup();
    }
}

function openCommentUpdatePopup(id, playlistId) {
    $.ajax({
        url: `/playlist/${playlistId}/comments/${id}`,
        type: 'GET',
        contentType: 'application/json',
        dataType: 'json',
        success: function(comment) {
            console.log('✅ Comment loaded:', comment);

            // ✅ 케밥 케이스 ID + CommentResponseDto 필드명 일치
            document.getElementById('modal-comment-id').value = id;
            document.getElementById('modal-content').value = comment.comment;
            document.getElementById('modal-writer').value = comment.nickname;

            const modal = document.getElementById('comment-update-popup');
            modal.classList.add('active');
            modal.style.display = 'block';

            document.addEventListener('keydown', handleEscKey);
        },
        error: function(xhr) {
            console.error('❌ Comment load error:', xhr.responseText);
            alert('댓글 정보를 불러올 수 없습니다.');
        }
    });
}

function closeCommentUpdatePopup() {
    const modal = document.getElementById('comment-update-popup');

    modal.classList.remove('active');
    modal.style.display = 'none';

    document.getElementById('modal-comment-id').value = '';
    document.getElementById('modal-content').value = '';
    document.getElementById('modal-writer').value = '';

    document.removeEventListener('keydown', handleEscKey);
}

function updateComment() {
    const playlistId = document.getElementById('playlist-id').value;
    const commentId = document.getElementById('modal-comment-id').value;
    const content = document.getElementById('modal-content').value;

    console.log('🔍 updateComment:', { playlistId, commentId, content });

    if (!content || content.trim() === "") {
        alert("공백 또는 입력하지 않은 부분이 있습니다.");
        return;
    }

    $.ajax({
        url: `/playlist/${playlistId}/comments/${commentId}`,
        type: 'PATCH',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify({
            comment: content
        }),
        beforeSend: function(xhr) {
            const token = localStorage.getItem('access_token');
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        },
        success: function(response) {
            console.log('✅ Comment updated:', response);
            alert('수정되었습니다.');
            closeCommentUpdatePopup();
            refreshComments(playlistId);
        },
        error: function(xhr) {
            console.error('❌ Update error:', xhr.responseText);
            alert('수정 실패했습니다.');
        }
    });
}

/* 6. 초기화 */
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 DOM loaded');
        initializeCommentListeners();
    });
} else {
    console.log('📄 DOM already loaded');
    initializeCommentListeners();
}

console.log('✅ All setup complete');
