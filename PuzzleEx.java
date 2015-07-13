package PuzzleGame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PuzzleEx extends JFrame {

    private JPanel panel;
    private BufferedImage source;
    private ArrayList<MyButton> buttons;

    ArrayList<Point> solution = new ArrayList();

    private Image image;
    private MyButton lastButton;
    private int width, height;
    private final int DESIRED_WIDTH = 500;  // Образ, который мы используем, чтобы сформировать масштабируется, чтобы
                                            // иметь требуемую ширину. С помощью метода getNewHeight ( )
                                            // мы вычисляем новую высоту, сохраняя соотношение изображения.
    private BufferedImage resized;
    private BufferedImage bImage;

    public PuzzleEx() {    //отвечает за то, что у нас в игре, сначала загруэаем картинку (отвечает за окно с выбором)
        try {
            bImage = loadImage();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Houston, we have a problem");
        }
        initUI();
/*        try {
            image = loadImage();
            System.out.println("GOOD");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void initUI() {

        solution.add(new Point(0, 0));      // Список-массив сохраняет правильный порядок кнопок,
        solution.add(new Point(0, 1));      // который формирует изображение.
        solution.add(new Point(0, 2));      // Каждая кнопка идентифицируется одной точке.
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList();

        panel = new JPanel();                            //Мы используем GridLayout, который хранить наши компоненты
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));     //Макет состоит из 4 строк и 3 столбцов

        try {
            source = bImage;
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h,
                    BufferedImage.TYPE_INT_ARGB);

        } catch (IOException ex) {
            Logger.getLogger(PuzzleEx.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 3; j++) {

                //CropImageFilter используется для резки прямоугольной формы с уже измененным размером
                //источника изображения. Он предназначен для использования в сочетании с объектом
                //FilteredImageSource, который производить укороченные версии существующих изображений.
                image = createImage(new FilteredImageSource(resized.getSource(),
                        new CropImageFilter(j * width / 3, i * height / 4,
                                (width / 3), height / 4)));
                MyButton button = new MyButton(image);
                button.putClientProperty("position", new Point(i, j));  //Кнопки идентифицируются по их положении.
                //Эти свойства используются, чтобы выяснить,
                //если у нас есть правильный порядок кнопок в окне

                if (i == 3 && j == 2) {
                    lastButton = new MyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }

        Collections.shuffle(buttons); //делаем рандом квадрытов (квадраты - это куски рисунка)
        buttons.add(lastButton);      //устанавливаем пустой квадрат

        //тут начинается сама логика игры
        for (int i = 0; i < 12; i++) {      // Все компоненты из списка кнопки размещены на панели.
            MyButton btn = buttons.get(i);  // Мы создаем некоторую серую окантовка кнопок
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction()); //и добавляем слушатель при нажатии
        }

        pack();
        setTitle("Puzzle");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private int getNewHeight(int w, int h) {         // getNewHeight()  - вычисляет высоту изображения на основе
        double ratio = DESIRED_WIDTH / (double) w;   // желаемой ширины. Соотношение сторон изображения сохраняется.
        int newHeight = (int) (h * ratio);           // Мы масштабировать изображение, используя эти значения.
        return newHeight;
    }


    public BufferedImage loadImage() throws IOException {    //отвечает за выбор  картинки
        BufferedImage bimg;
        JFileChooser fileChooser = new JFileChooser(".");   //создаем метод fileChooser, по названию можем догадатся, что
        fileChooser.setControlButtonsAreShown(false);       //это такое
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes()));
        File file;
        if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            bimg = ImageIO.read(file);
            return bimg;
        }
        return  null;
    }


    private BufferedImage resizeImage(BufferedImage originalImage,int width,int height,int type)throws IOException {

        BufferedImage resizedImage = new BufferedImage(width, height, type); //переобразовуем картинку в новый
        Graphics2D g = resizedImage.createGraphics();                       //BufferedImage с новыми размерами
        g.drawImage(originalImage, 0, 0, width, height, null);             //в новый BufferedImage
        g.dispose();

        return resizedImage;
    }

    private class ClickAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {

            int lidx = 0;
            for (MyButton button : buttons) {                   //Кнопки хранятся в списке массива.
                if (button.isLastButton()) {                    // Затем этот список отображается в сетке панели.
                    lidx = buttons.indexOf(button);
                }
            }

            JButton button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);                 // Мы получаем индексы последней кнопки и кнопки нажали.

            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);          // Они меняются местами с помощью Collections.swap( ),
                updateButtons();                                // если они являются смежными
            }
        }

        private void updateButtons() {          // Метод updateButtons() отображает список в сетке панели.


            panel.removeAll();                  // Во-первых, все компоненты удаляются с помощью метода RemoveAll().

            for (JComponent btn : buttons) {    //Цикл используется для перехода корыта список кнопок,


                panel.add(btn);             // чтобы добавить переупорядоченные кнопки назад менеджера компоновки панели.
            }

            panel.validate();                   //Наконец,метод проверки() реализует новый макет .
        }
    }

    private void checkSolution() {                                  // Проверка решения

        ArrayList<Point> current = new ArrayList();

        for (JComponent btn : buttons) {                            //Проверка происходит путем сравнения списка точек
            current.add((Point) btn.getClientProperty("position")); //правильно упорядоченных кнопок с текущего списка
            //Т.е. у нас есть список, который является правильным
        }                                //И пока наши пазлы не будут собраты в этом порядке - игра не будет завершенна
        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished",            //Когда пазл будет сложен правильно - нам появится
                    "Congratulation", JOptionPane.INFORMATION_MESSAGE); //сообщение, где будет написано это
        }
    }

    public static boolean compareList(java.util.List ls1, java.util.List ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }



    public static void main(String[] args) {                //запускаем наше приложение, собственно

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {

                PuzzleEx puzzle = new PuzzleEx();   //создаем новую игру
                puzzle.setVisible(true);
            }
        });
    }



}
