document.addEventListener("DOMContentLoaded", () => {
  const cards = document.querySelectorAll(".class-card");
  const carousel = document.querySelector(".class-carousel");
  const leftArrow = document.querySelector(".arrow.left");
  const rightArrow = document.querySelector(".arrow.right");

  let currentIndex = 1;

  const updateActiveCard = () => {
    cards.forEach((card, i) => {
      card.classList.toggle("active", i === currentIndex);
    });

    const cardWidth = cards[0].offsetWidth + 32;
    const scrollPosition = cardWidth * (currentIndex - 1);
    carousel.scrollTo({
      left: scrollPosition,
      behavior: "smooth"
    });
  };

  leftArrow.addEventListener("click", () => {
    if (currentIndex > 0) {
      currentIndex--;
      updateActiveCard();
    }
  });

  rightArrow.addEventListener("click", () => {
    if (currentIndex < cards.length - 1) {
      currentIndex++;
      updateActiveCard();
    }
  });

  updateActiveCard();
});
