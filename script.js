document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('palmReadingForm');
    const disclaimerBtn = document.getElementById('disclaimerBtn');
    const modal = document.getElementById('disclaimerModal');
    const closeBtn = document.querySelector('.close-btn');
    
    // Magical Dot Cursor
    const cursorDot = document.createElement('div');
    cursorDot.className = 'cursor-dot';
    document.body.appendChild(cursorDot);
    
    // Background Lighting Effect
    const cursorLight = document.createElement('div');
    cursorLight.className = 'cursor-light';
    document.body.appendChild(cursorLight);
    
    let mouseX = 0;
    let mouseY = 0;
    let dotX = 0;
    let dotY = 0;
    let lightX = 0;
    let lightY = 0;
    let trailElements = [];
    let lastTrailTime = 0;
    let isMoving = false;
    let moveTimeout;
    
    // Smooth cursor following
    function updateCursor() {
        dotX += (mouseX - dotX) * 0.2;
        dotY += (mouseY - dotY) * 0.2;
        
        cursorDot.style.left = dotX - 8 + 'px'; // Center the 16px dot
        cursorDot.style.top = dotY - 8 + 'px';
        
        // Update background lighting position
        lightX += (mouseX - lightX) * 0.03;
        lightY += (mouseY - lightY) * 0.03;
        
        cursorLight.style.left = lightX - 200 + 'px'; // Center the 400px light
        cursorLight.style.top = lightY - 200 + 'px';
        
        requestAnimationFrame(updateCursor);
    }
    
    // Mouse move handler
    document.addEventListener('mousemove', function(e) {
        mouseX = e.clientX;
        mouseY = e.clientY;
        
        // Activate lighting effect
        cursorLight.classList.add('active');
        isMoving = true;
        
        // Clear previous timeout
        clearTimeout(moveTimeout);
        
        // Set timeout to deactivate lighting when movement stops (longer fade for blending)
        moveTimeout = setTimeout(() => {
            cursorLight.classList.remove('active');
            isMoving = false;
        }, 500);
        
        // Create trail effect every 50ms
        const currentTime = Date.now();
        if (currentTime - lastTrailTime > 50) {
            createTrail(mouseX, mouseY);
            lastTrailTime = currentTime;
        }
    });
    
    // Create magical trail
    function createTrail(x, y) {
        const trail = document.createElement('div');
        trail.className = 'cursor-trail';
        trail.style.left = x - 3 + 'px';
        trail.style.top = y - 3 + 'px';
        document.body.appendChild(trail);
        
        trailElements.push(trail);
        
        // Remove trail after animation
        setTimeout(() => {
            if (trail.parentNode) {
                trail.parentNode.removeChild(trail);
                trailElements = trailElements.filter(t => t !== trail);
            }
        }, 1000);
        
        // Limit trail elements for performance
        if (trailElements.length > 20) {
            const oldestTrail = trailElements.shift();
            if (oldestTrail.parentNode) {
                oldestTrail.parentNode.removeChild(oldestTrail);
            }
        }
    }
    
    // Start cursor animation
    updateCursor();
    
    // Show modal when disclaimer button is clicked
    disclaimerBtn.addEventListener('click', function() {
        modal.style.display = 'block';
    });
    
    // Hide modal when close button is clicked
    closeBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });
    
    // Hide modal when clicking outside the modal content
    modal.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Basic validation
        const dominantHand = document.querySelector('input[name="dominantHand"]:checked');
        const leftPalm = document.getElementById('leftPalm').files[0];
        const rightPalm = document.getElementById('rightPalm').files[0];
        
        if (!dominantHand) {
            alert('The arcane forces demand knowledge of your dominant hand to proceed!');
            return;
        }
        
        if (!leftPalm || !rightPalm) {
            alert('Both sacred palm images are required to pierce the veil!');
            return;
        }
        
        // Check file types
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
        if (!allowedTypes.includes(leftPalm.type) || !allowedTypes.includes(rightPalm.type)) {
            alert('Only mystical image formats (JPEG, PNG, GIF) shall be accepted!');
            return;
        }
        
        // Simulate processing
        alert(`The ancient tomes stir with your ${dominantHand.value} dominant essence and palm mysteries. The veil parts... your destiny awaits.`);
        
        // In a real application, you would send the data to a server here
        // For now, we'll just reset the form
        form.reset();
    });
    
    // Add some magical effects
    const sections = document.querySelectorAll('section');
    sections.forEach(section => {
        section.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.02)';
            this.style.transition = 'transform 0.3s';
        });
        
        section.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });
});