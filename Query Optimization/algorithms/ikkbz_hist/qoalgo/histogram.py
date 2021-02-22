"""
approximations using histograms
"""


class Interval:
    def __init__(self, low, up):
        if low > up:
            raise Exception("Cannot create interval: low must be smaller or equal than up")
        self.low = low
        self.up = up

    def __len__(self):
        return self.up - self.low

    def contains(self, c):
        return self.low <= c <= self.up

    def intersect(self, interval):
        if interval.low > self.up or self.low > interval.up:
            return None
        return Interval(max(self.low, interval.low), min(self.up, interval.up))


class Histogram:

    def __init__(self):
        self.intervals = []

    def add_interval(self, low, up, elements):
        """
        Adds a new bucket to the histogram.
        :param low: lower bound
        :param up: upper bound
        :param elements: number of elements
        :return: self
        """
        self.intervals.append((Interval(low, up), elements))
        return self


def approx_equals_constant_verbose(histogram, c):
    numerator = []
    denominator = [elements for _, elements in histogram.intervals]
    for interval, elements in histogram.intervals:
        if interval.contains(c):
            numerator.append(elements)
    result = sum(numerator) / sum(denominator)
    return f'\\frac{{ {" + ".join(map(str, numerator))} }}{{ {" + ".join(map(str, denominator))} }} = {result:.4f}', result


def approx_greater_constant_verbose(histogram, c):
    numerator_tex = []
    numerator = []
    denominator = [elements for _, elements in histogram.intervals]
    for interval, elements in histogram.intervals:
        if interval.contains(c):
            numerator_tex.append(f'\\frac{{ {interval.up} - {c} }}{{ {interval.up} - {interval.low} }} \\cdot {elements}')
            numerator.append(((interval.up - c) / len(interval)) * elements)
        elif interval.low > c:
            numerator_tex.append(f'{elements}')
            numerator.append(elements)

    result = sum(numerator) / sum(denominator)
    return f'\\frac{{ {" + ".join(numerator_tex)} }}{{ {" + ".join(map(str, denominator))} }} = {result:.4f}', result


def approx_join_verbose(hist1, hist2):
    numerator_tex = []
    numerator = []
    denominator_h1 = [elements for _, elements in hist1.intervals]
    denominator_h2 = [elements for _, elements in hist2.intervals]

    for i1, elements1 in hist1.intervals:
        for i2, elements2 in hist2.intervals:
            iprime = i1.intersect(i2)
            if iprime is None:
                continue

            numerator_tex.append(
                f'\\frac{{ {len(iprime)} }}{{ {len(i1)} }} \\cdot {elements1} \\cdot '
                f'\\frac{{ {len(iprime)} }}{{ {len(i2)} }} \\cdot {elements2}'
            )
            numerator.append(((len(iprime) / len(i1)) * elements1) * ((len(iprime) / len(i2)) * elements2))
    result = sum(numerator) / (sum(denominator_h1) * sum(denominator_h2))
    return f'\\frac{{ {" + ".join(numerator_tex)} }}' \
           f'{{ \\left( {" + ".join(map(str, denominator_h1))} \\right) \\cdot ' \
           f'\\left( {" + ".join(map(str, denominator_h2))} \\right) }} '\
           f' = {result:.4f}', result
