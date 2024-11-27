
function openEditModal() {
    document.getElementById('editModal').style.display = "block";
}
function closeEditModal() {
    document.getElementById('editModal').style.display = "none";
}
window.onclick = function(event) {
    if (event.target == document.getElementById('editModal')) {
        closeEditModal();
    }
}
function openEditCommentModal(commentId, postId) {

    const modal = document.getElementById("editCommentModal");
    document.getElementById("edit-comment-id").value = commentId;
    document.getElementById("edit-post-id").value = postId;

    // 텍스트 영역 초기화 (내용 비우기)
    document.getElementById("edit-comment-content").value = "";

    // 모달 열기
    document.getElementById("editCommentModal").style.display = "block";
}
function closeEditCommentModal() {
    const modal = document.getElementById("editCommentModal");
    document.getElementById("edit-comment-content").value = "";
    modal.style.display = "none";
    modal.classList.remove("show");

}
function updateCommentAjax() {
    const commentId = document.getElementById("edit-comment-id").value;
    const postId = document.getElementById("edit-post-id").value;
    const newContent = document.getElementById("edit-comment-content").value;
    if (newContent.trim() === '') {
        alert('댓글 내용을 입력해주세요.');
        return;
    }
    fetch(`/comments/${commentId}/update`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            postId: postId,
            content: newContent
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('댓글이 수정되었습니다.');
            location.reload();
        } else {
            alert('작성자만 수정 가능합니다.');
        }
    })
    .catch(error => {
        console.error('수정 중 오류 발생:', error);
        alert('오류가 발생했습니다. 다시 시도해주세요.');
    });
}
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.delete-button').forEach(button => {
        button.addEventListener('click', async function(event) {
            // 기본 동작 차단
            event.preventDefault();
            event.stopPropagation();

            const commentId = button.getAttribute('data-comment-id');
            const commentAuthor = button.getAttribute('data-comment-author');
            const currentUser = button.getAttribute('data-current-user');
            const postId = button.closest('form').querySelector('input[name="postId"]').value;

            if (confirm('정말로 댓글을 삭제하시겠습니까?')) {
                if (commentAuthor !== currentUser) {
                    alert('작성자만 댓글을 삭제할 수 있습니다.');
                    return;
                }

                try {
                    const response = await fetch(`/comments/${commentId}/delete`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({
                            '_method': 'delete',
                            'postId': postId
                        })
                    });

                    const data = await response.json();

                    if (data.success) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }

                    // 강제 리다이렉트
                    window.location.href = `/posts/${postId}`;
                } catch (error) {
                    console.error('삭제 중 오류 발생:', error);
                    alert('댓글 삭제 중 오류가 발생했습니다.');
                    window.location.href = `/posts/${postId}`;
                }
            }
        });
    });
});
function toggleReplyForm(commentId) {
    var replyForm = document.getElementById('reply-form-container-' + commentId);
    replyForm.style.display = replyForm.style.display === 'none' ? 'block' : 'none';
}
function openEditReplyModal2(buttonElement) {
    const replyId = buttonElement.getAttribute("data-reply-id");
    const modalId = `edit-reply-modal2-${replyId}`;
    const contentFieldId = `edit-reply-content2-${replyId}`;
    const modal = document.getElementById(modalId);
    const contentField = document.getElementById(contentFieldId);
    if (!modal || !contentField) {
        console.error(`Modal or content field not found.`);
        return;
    }
    contentField.value = "";
    modal.style.display = 'block';
    modal.classList.add("show");
    modal.style.position = "fixed";
}
function saveReply2(replyId) {
    const content = document.getElementById(`edit-reply-content2-${replyId}`).value;
    const postId = document.querySelector(`#edit-reply-form2-${replyId} input[name='postId']`).value;
    const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
    fetch(`/comments/reply/${replyId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': csrfToken
        },
        body: new URLSearchParams({
            'postId': postId,
            'content': content
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            window.location.replace(`/posts/${postId}?message=${encodeURIComponent(data.message)}`);
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('수정 중 오류 발생:', error);
        alert('답글 수정 중 오류가 발생했습니다.');
    });
}
function closeEditReplyModal2(replyId) {
    const modal = document.getElementById(`edit-reply-modal2-${replyId}`);
    modal.style.display = 'none';
    modal.classList.remove("show");
}

document.addEventListener('DOMContentLoaded', () => {
    // 모든 '답글 삭제' 버튼에 대해 클릭 이벤트 바인딩
    document.querySelectorAll('.delete-reply-button').forEach(button => {
        button.addEventListener('click', async function(event) {
            // 기본 동작 차단
            event.preventDefault();
            event.stopPropagation();

            // 버튼에서 필요한 데이터 가져오기
            const replyId = button.getAttribute('data-reply-id');
            const replyAuthor = button.getAttribute('data-reply-author');
            const currentUser = button.getAttribute('data-current-user');
            const postId = document.querySelector('input[name="postId"]').value;

            // 삭제 확인
            if (confirm('정말로 답글을 삭제하시겠습니까?')) {
                // 작성자 체크
                if (replyAuthor !== currentUser) {
                    alert('작성자만 답글을 삭제할 수 있습니다.');
                    return;
                }

                try {
                    // 비동기 삭제 요청
                    const response = await fetch(`/comments/reply/${replyId}/delete`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({
                            '_method': 'delete',
                            'postId': postId
                        })
                    });

                    // JSON 응답 처리
                    const data = await response.json();

                    if (data.success) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }

                    // 페이지 새로고침 (또는 필요한 경우 댓글 목록만 새로 로드)
                    window.location.href = `/posts/${postId}`;
                } catch (error) {
                    console.error('답글 삭제 중 오류 발생:', error);
                    alert('답글 삭제 중 오류가 발생했습니다.');
                    window.location.href = `/posts/${postId}`;
                }
            }
        });
    });
});

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.delete-post-button').forEach(button => {
        button.addEventListener('click', async function(event) {
            // 기본 동작 차단
            event.preventDefault();
            event.stopPropagation();

            const postId = button.getAttribute('data-post-id');
            const postAuthor = button.getAttribute('data-post-author');
            const currentUser = button.getAttribute('data-current-user');

            if (confirm('정말로 게시글을 삭제하시겠습니까?')) {
                if (postAuthor !== currentUser && currentUser !== '관리자') {
                    alert('작성자만 게시글을 삭제할 수 있습니다.');
                    return;
                }

                try {
                    const response = await fetch(`/posts/${postId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({
                            '_method': 'delete'
                        })
                    });

                    const data = await response.json();

                    if (data.success) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }

                    // 강제 리다이렉트
                    window.location.href = '/'; // 삭제 후 홈 페이지로 리다이렉트
                } catch (error) {
                    console.error('삭제 중 오류 발생:', error);
                    alert('게시글 삭제 중 오류가 발생했습니다.');
                    window.location.href = '/'; // 오류 발생 시 홈 페이지로 리다이렉트
                }
            }
        });
    });
});


