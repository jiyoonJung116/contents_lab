document.getElementById('writeForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const formData = new FormData(this);

    fetch('/api/community/save', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
    if (data.status === 'success') {
        alert(data.message);
        location.href = '/community';
    } else {
        alert(data.message);
        if (data.message === '로그인이 필요합니다.') {
            location.href = '/login';
        }
    }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('저장 중 오류가 발생했습니다.');
    });
});